/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.mcpserver.utility.LoggingUtility.log;

import com.fasterxml.jackson.databind.JsonNode;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import java.sql.Connection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.tools.FunctionCallback;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.ToolUtility;

@Component
public class ToolHelper {

  public static class ToolCallHandler
      implements BiFunction<McpSyncServerExchange, CallToolRequest, CallToolResult> {

    private final FunctionCallback functionCallback;

    public ToolCallHandler(final FunctionCallback functionCallback) {
      this.functionCallback = requireNonNull(functionCallback, "No function callback provided");
    }

    @Override
    public CallToolResult apply(
        final McpSyncServerExchange exchange, final CallToolRequest request) {
      try {
        final String arguments = ModelOptionsUtils.toJsonString(request.arguments());
        log(
            exchange,
            String.format(
                "Executing:%n%s", functionCallback.toCallObject(arguments).toPrettyString()));
        final Connection connection = ConnectionService.getConnection();
        final FunctionReturn functionReturn = functionCallback.execute(arguments, connection);
        return new CallToolResult(List.of(new TextContent(functionReturn.get())), false);
      } catch (final Exception e) {
        return new CallToolResult(List.of(new TextContent(e.getMessage())), true);
      }
    }

    public String name() {
      return functionCallback.getFunctionName().getName();
    }
  }

  private static final Logger LOGGER = Logger.getLogger(ToolHelper.class.getCanonicalName());

  @Autowired private Catalog catalog;

  public McpServerFeatures.SyncToolSpecification toSyncToolSpecification(
      final FunctionDefinition<?> functionDefinition) {

    final Tool tool = toTool(functionDefinition);
    final FunctionCallback functionCallback =
        new FunctionCallback(functionDefinition.getName(), catalog);
    final ToolCallHandler toolCallHandler = new ToolCallHandler(functionCallback);

    return new McpServerFeatures.SyncToolSpecification(tool, null, toolCallHandler);
  }

  private Tool toTool(final FunctionDefinition<?> functionDefinition) {
    requireNonNull(functionDefinition, "No function definition provided");
    final JsonNode parametersSchemaNode =
        ToolUtility.extractParametersSchemaNode(functionDefinition.getParametersClass());
    final Tool tool =
        Tool.builder()
            .name(functionDefinition.getName())
            .description(functionDefinition.getDescription())
            .inputSchema(parametersSchemaNode.toString())
            .build();
    return tool;
  }
}
