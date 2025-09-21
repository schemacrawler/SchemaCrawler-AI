/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.stereotype.Component;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.ToolUtility;

@Component
public class ToolHelper {

  private static final Logger LOGGER = Logger.getLogger(ToolHelper.class.getCanonicalName());

  public ToolDefinition toToolDefinition(final FunctionDefinition<?> functioDefinition) {
    requireNonNull(functioDefinition, "No function definition provided");
    final ToolDefinition toolDefinition =
        ToolDefinition.builder()
            .name(functioDefinition.getName())
            .description(functioDefinition.getDescription())
            .inputSchema(
                ToolUtility.extractParametersSchemaNode(functioDefinition.getParametersClass())
                    .toString())
            .build();
    return toolDefinition;
  }
}
