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

package schemacrawler.tools.command.utility.simpleopenai;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.Objects.requireNonNull;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.common.function.FunctionCall;
import io.github.sashirestela.openai.common.function.FunctionExecutor;
import io.github.sashirestela.openai.common.tool.ToolCall;
import io.github.sashirestela.openai.domain.chat.Chat;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.AssistantMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.ResponseMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.SystemMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.ToolMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.UserMessage;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import io.github.sashirestela.openai.domain.chat.ChatRequest.Modality;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.aichat.ChatAssistant;
import schemacrawler.tools.command.aichat.embeddings.EmbeddingService;
import schemacrawler.tools.command.aichat.embeddings.QueryService;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;
import schemacrawler.tools.command.aichat.utility.FunctionExecutionUtility;
import us.fatehi.utility.string.StringFormat;

public final class SimpleOpenAIChatAssistant implements ChatAssistant {

  private static final Logger LOGGER = Logger.getLogger(SimpleOpenAIChatAssistant.class.getCanonicalName());

  private final AiChatCommandOptions commandOptions;
  private final FunctionExecutor functionExecutor;
  private final SimpleOpenAI service;
  private final QueryService queryService;
  private final ChatHistory chatHistory;
  private final boolean useMetadata;
  private final Catalog catalog;
  private final Connection connection;
  private boolean shouldExit;

  public SimpleOpenAIChatAssistant(
      final AiChatCommandOptions commandOptions,
      final Catalog catalog,
      final Connection connection) {

    this.commandOptions = requireNonNull(commandOptions, "AI chat options not provided");
    this.catalog = requireNonNull(catalog, "No catalog provided");
    this.connection = requireNonNull(connection, "No connection provided");

    functionExecutor = SimpleOpenAIUtility.toolsList();
    service = SimpleOpenAIUtility.newService(commandOptions);

    final EmbeddingService embeddingService = new SimpleOpenAIEmbeddingService(service);
    queryService = new QueryService(embeddingService);
    queryService.addTables(catalog.getTables());

    useMetadata = commandOptions.isUseMetadata();
    chatHistory = new ChatHistory(commandOptions.getContext(), new ArrayList<>());
  }

  /**
   * Send prompt to AI chat API and get completions.
   *
   * @param prompt Input prompt.
   */
  @Override
  public String chat(final String prompt) {

    final List<ChatMessage> completions = new ArrayList<>();

    try {

      chatHistory.add(UserMessage.of(prompt));

      final List<ChatMessage> messages = chatHistory.toList();

      if (useMetadata) {
        final Collection<String> chatMessages = queryService.query(prompt);
        final List<SystemMessage> systemMessages =
            chatMessages.stream().map(SystemMessage::of).collect(Collectors.toList());
        messages.addAll(systemMessages);
      }

      final ChatRequest chatRequest =
          ChatRequest.builder()
              .model(commandOptions.getModel())
              .messages(messages)
              .tools(functionExecutor.getToolFunctions())
              .temperature(1.0)
              .modality(Modality.TEXT)
              .build();
      final CompletableFuture<Chat> futureChat = service.chatCompletions().create(chatRequest);
      final Chat chatResponse = futureChat.join();

      LOGGER.log(Level.INFO, new StringFormat("Token usage: %s", chatResponse.getUsage()));
      // Assume only one message was returned, since we asked for only one
      final ResponseMessage responseMessage = chatResponse.firstMessage();
      chatHistory.add(responseMessage);

      final List<ToolCall> toolCalls = responseMessage.getToolCalls();
      if (toolCalls != null && !toolCalls.isEmpty()) {
        final ToolCall toolCall = toolCalls.get(0);
        final FunctionCall functionCall = toolCall.getFunction();
        LOGGER.log(
            Level.INFO,
            new StringFormat(
                "Function call: %s(%s)", functionCall.getName(), functionCall.getArguments()));
        requireNonNull(functionCall, "No function call provided");
        final String returnString =
            FunctionExecutionUtility.execute(
                functionCall.getName(), functionCall.getArguments(), catalog, connection);
        completions.add(ToolMessage.of(returnString, toolCall.getId()));
        // Add to chat history
        chatHistory.add(ToolMessage.of(returnString, toolCall.getId()));
      } else {
        completions.add(AssistantMessage.of(responseMessage.getContent()));
      }
    } catch (final Exception e) {
      e.printStackTrace();
      LOGGER.log(Level.INFO, e.getMessage(), e);
    }

    shouldExit = SimpleOpenAIUtility.isExitCondition(completions);
    final String response = SimpleOpenAIUtility.getResponse(completions);
    return response;
  }

  @Override
  public void close() {}

  @Override
  public boolean shouldExit() {
    return shouldExit;
  }
}
