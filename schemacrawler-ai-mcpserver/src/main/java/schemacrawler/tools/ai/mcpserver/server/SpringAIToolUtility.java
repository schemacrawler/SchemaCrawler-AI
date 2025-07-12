/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */



package schemacrawler.tools.ai.mcpserver.server;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.tools.FunctionDefinitionRegistry;
import schemacrawler.tools.ai.tools.FunctionReturnType;
import schemacrawler.tools.ai.tools.ToolSpecification;
import schemacrawler.tools.ai.tools.ToolUtility;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class SpringAIToolUtility {

  private static final Logger LOGGER =
      Logger.getLogger(SpringAIToolUtility.class.getCanonicalName());

  public static List<ToolCallback> toolCallbacks(final List<ToolDefinition> tools) {
    Objects.requireNonNull(tools, "Tools must not be null");

    final boolean isDryRun = ConfigurationManager.getInstance().isDryRun();
    final Catalog catalog;
    final Connection connection;
    if (isDryRun) {
      catalog = null;
      connection = null;
    } else {
      final ConnectionService connectionService = ConnectionService.getInstance();
      catalog = connectionService.catalog();
      connection = connectionService.connection();
    }

    final List<ToolCallback> toolCallbacks = new ArrayList<>();
    for (final ToolDefinition toolDefinition : tools) {
      toolCallbacks.add(new SpringAIToolCallback(isDryRun, toolDefinition, catalog, connection));
    }
    return toolCallbacks;
  }

  public static List<ToolDefinition> tools() {
    final FunctionDefinitionRegistry functionDefinitionRegistry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
    final List<ToolDefinition> toolDefinitions = new ArrayList<>();
    for (final ToolSpecification toolSpecification :
        functionDefinitionRegistry.getToolSpecifications(FunctionReturnType.JSON)) {
      final ToolDefinition toolDefinition = toToolDefinition(toolSpecification);
      toolDefinitions.add(toolDefinition);
    }
    // Add lint function, in text, not JSON
    final ToolDefinition lintDefinition =
        toToolDefinition(
            ToolUtility.toToolSpecification(
                functionDefinitionRegistry.lookupFunctionDefinition("lint").get()));
    toolDefinitions.add(lintDefinition);

    return toolDefinitions;
  }

  private static ToolDefinition toToolDefinition(final ToolSpecification toolSpecification) {
    final ToolDefinition toolDefinition =
        ToolDefinition.builder()
            .name(toolSpecification.name())
            .description(toolSpecification.description())
            .inputSchema(toolSpecification.getParametersAsString())
            .build();
    return toolDefinition;
  }

  private SpringAIToolUtility() {
    // Prevent instantiation
  }
}
