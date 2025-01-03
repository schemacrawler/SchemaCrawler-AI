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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutor;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.command.aichat.ChatAssistant;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;
import schemacrawler.tools.command.aichat.utility.lanchain4j.AiModelFactoryUtility.AiModelFactory;
import us.fatehi.utility.string.StringFormat;

public class Langchain4JChatAssistant implements ChatAssistant {

  interface Assistant {
    Response<AiMessage> chat(String prompt);
  }

  private static final Logger LOGGER =
      Logger.getLogger(Langchain4JChatAssistant.class.getCanonicalName());

  private final Map<ToolSpecification, ToolExecutor> toolSpecificationsMap;
  private final Assistant assistant;
  private final ChatMemory chatMemory;
  private boolean shouldExit;

  public Langchain4JChatAssistant(
      final AiChatCommandOptions commandOptions,
      final Catalog catalog,
      final Connection connection) {

    requireNonNull(commandOptions, "AI chat options not provided");
    requireNonNull(catalog, "No catalog provided");
    requireNonNull(connection, "No connection provided");

    final AiModelFactory modelFactory = AiModelFactoryUtility.chooseAiModelFactory(commandOptions);
    if (modelFactory == null) {
      throw new SchemaCrawlerException("No models found");
    }

    toolSpecificationsMap = Langchain4JUtility.toolsList(catalog, connection);
    chatMemory = modelFactory.newChatMemory();

    final ChatLanguageModel model = modelFactory.newChatLanguageModel();
    assistant =
        AiServices.builder(Assistant.class)
            .chatLanguageModel(model)
            .tools(toolSpecificationsMap)
            .chatMemory(chatMemory)
            .contentRetriever(new CatalogContentRetriever(commandOptions, catalog))
            .build();
  }

  /**
   * Send prompt to AI chat API and get completions.
   *
   * @param prompt Input prompt.
   */
  @Override
  public String chat(final String prompt) {

    final Response<AiMessage> response = assistant.chat(prompt);
    final TokenUsage tokenUsage = response.tokenUsage();
    LOGGER.log(Level.INFO, new StringFormat("%s", tokenUsage));

    shouldExit = Langchain4JUtility.isExitCondition(chatMemory.messages());

    return response.content().text();
  }

  @Override
  public void close() {}

  @Override
  public boolean shouldExit() {
    return shouldExit;
  }
}
