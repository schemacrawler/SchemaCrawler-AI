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

package schemacrawler.tools.command.aichat.mcp;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.aichat.tools.FunctionDefinitionRegistry;
import schemacrawler.tools.command.aichat.tools.ToolSpecification;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class SpringAIToolUtility {

  private static final Logger LOGGER =
      Logger.getLogger(SpringAIToolUtility.class.getCanonicalName());
  public static boolean isDryRun = false;

  public static List<ToolCallback> toolCallbacks(final List<ToolDefinition> tools) {
    Objects.requireNonNull(tools, "Tools must not be null");
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

    final List<ToolDefinition> toolDefinitions = new ArrayList<>();
    for (final ToolSpecification toolSpecification :
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry().getToolSpecifications()) {

      final ToolDefinition toolDefinition =
          ToolDefinition.builder()
              .name(toolSpecification.name())
              .description(toolSpecification.description())
              .inputSchema(toolSpecification.getParametersAsString())
              .build();
      toolDefinitions.add(toolDefinition);
    }

    return toolDefinitions;
  }

  private SpringAIToolUtility() {
    // Prevent instantiation
  }
}
