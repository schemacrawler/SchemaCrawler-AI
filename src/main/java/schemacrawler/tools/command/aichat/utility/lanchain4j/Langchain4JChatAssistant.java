/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
 */

package schemacrawler.tools.command.aichat.utility.lanchain4j;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.service.tool.ToolExecutor;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.command.aichat.ChatAssistant;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;
import schemacrawler.tools.command.aichat.utility.lanchain4j.AiModelFactoryUtility.AiModelFactory;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.string.StringFormat;

public class Langchain4JChatAssistant implements ChatAssistant {

  private static final AiMessage TOOL_CALL_MEMORY_MESSAGE =
      AiMessage.from("(Information on tool calls is redacted for security reasons.)");

  private static final Logger LOGGER =
      Logger.getLogger(Langchain4JChatAssistant.class.getCanonicalName());

  private final ChatLanguageModel model;
  private final ChatMemory chatMemory;
  private final List<ToolSpecification> toolSpecifications;
  private final Map<String, ToolExecutor> toolExecutors;
  private final ContentRetriever contentRetriever;
  private final String metadataPriming;
  private final int chatContextWindowSize;
  private boolean shouldExit;

  public Langchain4JChatAssistant(
      final AiChatCommandOptions aiChatOptions,
      final Catalog catalog,
      final Connection connection) {

    requireNonNull(aiChatOptions, "AI chat options not provided");
    requireNonNull(catalog, "No catalog provided");
    requireNonNull(connection, "No connection provided");

    final AiModelFactory modelFactory = AiModelFactoryUtility.chooseAiModelFactory(aiChatOptions);
    if (modelFactory == null) {
      throw new SchemaCrawlerException("No models found");
    }

    chatContextWindowSize = aiChatOptions.context();

    model = modelFactory.newChatLanguageModel();
    chatMemory = modelFactory.newChatMemory();

    final boolean useMetadata = aiChatOptions.useMetadata();
    if (useMetadata) {
      final EmbeddingModel embeddingModel;
      if (modelFactory.hasEmbeddingModel()) {
        embeddingModel = modelFactory.newEmbeddingModel();
      } else {
        embeddingModel = null;
      }
      contentRetriever = new FullTextCatalogContentRetriever(embeddingModel, catalog);
    } else {
      contentRetriever = query -> Collections.emptyList();
    }

    toolSpecifications = Langchain4JUtility.tools();
    toolExecutors = Langchain4JUtility.toolExecutors(catalog, connection);

    metadataPriming = IOUtility.readResourceFully("/metadata-priming.txt");
  }

  /**
   * Send prompt to AI chat API and get completions.
   *
   * @param prompt Input prompt.
   */
  @Override
  public String chat(final String prompt) {

    try {
      if (isBlank(prompt)) {
        return "";
      }
      chatMemory.add(UserMessage.from(prompt));
      final List<ChatMessage> messages = getChatContext();
      final SystemMessage systemMessage = createSystemMessage(prompt);
      messages.add(0, systemMessage);

      // Call the AI service
      final ChatRequest chatRequest =
          ChatRequest.builder().messages(messages).toolSpecifications(toolSpecifications).build();
      final ChatResponse response = model.chat(chatRequest);
      final TokenUsage tokenUsage = response.tokenUsage();
      LOGGER.log(Level.INFO, new StringFormat("%s", tokenUsage));

      final AiMessage aiMessage = response.aiMessage();
      final String answer;
      if (aiMessage.hasToolExecutionRequests()) {
        final StringBuilder buffer = new StringBuilder();
        final List<ToolExecutionRequest> executionRequests = aiMessage.toolExecutionRequests();
        for (final ToolExecutionRequest toolExecutionRequest : executionRequests) {
          final String functionName = toolExecutionRequest.name();
          shouldExit = !shouldExit && functionName.startsWith("exit");
          final ToolExecutor toolExecutor = toolExecutors.get(functionName);
          final String toolExecutionResult = toolExecutor.execute(toolExecutionRequest, null);
          buffer.append(toolExecutionResult);
        }
        answer = buffer.toString();
        chatMemory.add(TOOL_CALL_MEMORY_MESSAGE);
      } else {
        // If no tools need to be executed, return as-is
        answer = aiMessage.text();
        chatMemory.add(aiMessage);
      }

      return answer;

    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, e, new StringFormat("Exception handling prompt:%n%s", prompt));
      e.printStackTrace();
      return "There was a problem. Please try again.";
    }
  }

  @Override
  public void close() {}

  @Override
  public boolean shouldExit() {
    return shouldExit;
  }

  private SystemMessage createSystemMessage(final String prompt) {

    final StringBuilder buffer = new StringBuilder();
    buffer.append(metadataPriming).append("\n");
    final List<Content> contents = contentRetriever.retrieve(Query.from(prompt));
    for (final Content content : contents) {
      buffer.append("\n").append(content.textSegment().text());
    }

    return SystemMessage.from(buffer.toString());
  }

  private List<ChatMessage> getChatContext() {
    final List<ChatMessage> messages = new ArrayList<>(chatMemory.messages());
    final int size = messages.size();
    final int startIndex = Math.max(0, size - chatContextWindowSize);
    final List<ChatMessage> chatContext = messages.subList(startIndex, size);
    return new ArrayList<>(chatContext);
  }
}
