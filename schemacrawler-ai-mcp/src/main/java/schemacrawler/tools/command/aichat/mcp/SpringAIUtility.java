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

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.lang.Nullable;
import schemacrawler.tools.command.aichat.tools.FunctionDefinitionRegistry;
import schemacrawler.tools.command.aichat.tools.ToolSpecification;
import us.fatehi.utility.UtilityMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@UtilityMarker
public final class SpringAIUtility {

  private static final Logger LOGGER = Logger.getLogger(SpringAIUtility.class.getCanonicalName());

  private SpringAIUtility() {
    // Prevent instantiation
  }

  public static List<ToolCallback> toolCallbacks(final List<ToolDefinition> tools) {
    Objects.requireNonNull(tools, "Tools must not be null");
    final List<ToolCallback> toolCallbacks = new ArrayList<>();
    for (final ToolDefinition toolDefinition : tools) {
      toolCallbacks.add(new SpringAIToolCallback(toolDefinition));
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

  public record SpringAIToolCallback(ToolDefinition toolDefinition) implements ToolCallback {

    public SpringAIToolCallback {
      Objects.requireNonNull(toolDefinition, "Tool definition must not be null");
    }

    @Override
    public ToolDefinition getToolDefinition() {
      return toolDefinition;
    }

    @Override
    public String call(final String toolInput) {
      final String callMessage =
          String.format(
              "Call to <%s>%n%s%nTool was successfully executed with no return value.",
              toolDefinition.name(), toolInput);
      System.out.println(callMessage);
      return callMessage;
    }

    @Override
    public String call(final String toolInput, @Nullable final ToolContext tooContext) {
      return call(toolInput);
    }
  }
}
