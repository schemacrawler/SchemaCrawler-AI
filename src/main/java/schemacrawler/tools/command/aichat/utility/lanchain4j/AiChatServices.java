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
import static java.util.Objects.requireNonNull;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutor;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;
import schemacrawler.tools.command.aichat.utility.lanchain4j.AiModelFactoryUtility.AiModelFactory;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class AiChatServices {

  public static AiChatService aiChatServiceFrom(
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

    final ChatMemory chatMemory = modelFactory.newChatMemory();

    final ChatLanguageModel model = modelFactory.newChatLanguageModel();
    final Map<ToolSpecification, ToolExecutor> toolSpecificationsMap =
        Langchain4JUtility.toolsList(catalog, connection);

    final AiChatService aiChatService;

    if (commandOptions.isUseMetadata()) {
      final CatalogContentRetriever contentRetriever =
          new CatalogContentRetriever(commandOptions, catalog);
      aiChatService =
          AiServices.builder(AiChatService.class)
              .chatLanguageModel(model)
              .chatMemory(chatMemory)
              .tools(toolSpecificationsMap)
              .contentRetriever(contentRetriever)
              .build();
    } else {
      aiChatService = new NoMetadataAiChatService(model, chatMemory, toolSpecificationsMap);
    }

    return aiChatService;
  }

  private AiChatServices() {
    // Prevent instantiation
  }
}
