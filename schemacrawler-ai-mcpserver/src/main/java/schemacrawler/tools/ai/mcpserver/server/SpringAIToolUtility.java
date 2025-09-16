/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.tool.definition.ToolDefinition;
import schemacrawler.tools.ai.tools.FunctionDefinitionRegistry;
import schemacrawler.tools.ai.tools.FunctionReturnType;
import schemacrawler.tools.ai.tools.ToolSpecification;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class SpringAIToolUtility {

  public static List<ToolDefinition> tools() {
    final FunctionDefinitionRegistry functionDefinitionRegistry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
    final List<ToolDefinition> toolDefinitions = new ArrayList<>();
    for (final ToolSpecification toolSpecification :
        functionDefinitionRegistry.getToolSpecifications(FunctionReturnType.JSON)) {
      final ToolDefinition toolDefinition = toToolDefinition(toolSpecification);
      toolDefinitions.add(toolDefinition);
    }

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
