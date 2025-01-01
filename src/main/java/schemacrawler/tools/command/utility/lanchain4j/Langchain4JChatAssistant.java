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

package schemacrawler.tools.command.utility.lanchain4j;

import java.sql.Connection;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutor;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.aichat.ChatAssistant;
import schemacrawler.tools.command.aichat.embeddings.EmbeddingService;
import schemacrawler.tools.command.aichat.embeddings.QueryService;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;

public class Langchain4JChatAssistant implements ChatAssistant {

  interface Assistant {
    String chat(String prompt);
  }

  private static final Logger LOGGER =
      Logger.getLogger(Langchain4JChatAssistant.class.getCanonicalName());

  private final Map<ToolSpecification, ToolExecutor> toolSpecificationsMap;
  private final Assistant assistant;
  private final QueryService queryService;
  private final ChatMemory chatMemory;
  private final boolean useMetadata;
  private boolean shouldExit;

  public Langchain4JChatAssistant(
      final AiChatCommandOptions commandOptions,
      final Catalog catalog,
      final Connection connection) {

    requireNonNull(commandOptions, "AI chat options not provided");
    requireNonNull(catalog, "No catalog provided");
    requireNonNull(connection, "No connection provided");

    toolSpecificationsMap = Langchain4JUtility.toolsList(catalog, connection);

    chatMemory =
        TokenWindowChatMemory.builder()
            .maxTokens(8_000, new OpenAiTokenizer(commandOptions.getModel()))
            .build();

    final ChatLanguageModel model =
        OpenAiChatModel.builder()
            .apiKey(commandOptions.getApiKey())
            .modelName(commandOptions.getModel())
            .strictTools(
                true) // https://docs.langchain4j.dev/integrations/language-models/open-ai#structured-outputs-for-tools
            .build();

    assistant =
        AiServices.builder(Assistant.class)
            .chatLanguageModel(model)
            .tools(toolSpecificationsMap)
            .chatMemory(chatMemory)
            .build();

    final EmbeddingService embeddingService =
        new Langchain4JEmbeddingService(commandOptions.getApiKey());
    queryService = new QueryService(embeddingService);
    queryService.addTables(catalog.getTables());

    useMetadata = commandOptions.isUseMetadata();
  }

  /**
   * Send prompt to AI chat API and get completions.
   *
   * @param prompt Input prompt.
   */
  @Override
  public String chat(final String prompt) {

    if (useMetadata) {
      final Collection<String> chatMessages = queryService.query(prompt);
      chatMessages.stream().map(SystemMessage::from).forEach(message -> chatMemory.add(message));
    }

    return assistant.chat(prompt);
  }

  @Override
  public void close() {}

  @Override
  public boolean shouldExit() {
    return shouldExit;
  }
}
