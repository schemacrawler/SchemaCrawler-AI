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
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import java.sql.Connection;
import java.util.List;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.functions.ExceptionFunctionReturn;
import schemacrawler.tools.ai.tools.FunctionCallback;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.ToolUtility;

@Component
public class ToolHelper {

  private static class ToolCallHandler
      implements BiFunction<McpSyncServerExchange, CallToolRequest, CallToolResult> {

    private final FunctionCallback<? extends FunctionParameters> functionCallback;

    public ToolCallHandler(final FunctionCallback<? extends FunctionParameters> functionCallback) {
      this.functionCallback = requireNonNull(functionCallback, "No function callback provided");
    }

    @Override
    public CallToolResult apply(
        final McpSyncServerExchange exchange, final CallToolRequest request) {
      FunctionReturn functionReturn;
      try {
        final String arguments = mapper.writeValueAsString(request.arguments());
        log(
            exchange,
            String.format(
                "Executing:%n%s", functionCallback.toCallObject(arguments).toPrettyString()));
        final Connection connection = ConnectionService.getConnection();
        functionReturn = functionCallback.execute(arguments, connection);
      } catch (final Exception e) {
        functionReturn = new ExceptionFunctionReturn(e);
      }
      final List<Content> content = createContent(functionReturn);
      final boolean inError = functionReturn instanceof ExceptionFunctionReturn;
      final CallToolResult callToolResult =
          CallToolResult.builder().content(content).isError(inError).build();
      return callToolResult;
    }

    private List<Content> createContent(final FunctionReturn functionReturn) {
      Content content;
      if (functionReturn == null) {
        content = new TextContent("");
      } else {
        content = new TextContent(functionReturn.get());
      }
      return List.of(content);
    }
  }

  @Autowired private Catalog catalog;

  public <P extends FunctionParameters>
      McpServerFeatures.SyncToolSpecification toSyncToolSpecification(
          final FunctionDefinition<P> functionDefinition) {

    final McpSchema.Tool tool = toTool(functionDefinition);
    final FunctionCallback<P> functionCallback =
        new FunctionCallback<>(functionDefinition, catalog);
    final ToolCallHandler toolCallHandler = new ToolCallHandler(functionCallback);

    return new McpServerFeatures.SyncToolSpecification(tool, null, toolCallHandler);
  }

  private <P extends FunctionParameters> Tool toTool(
      final FunctionDefinition<P> functionDefinition) {
    requireNonNull(functionDefinition, "No function definition provided");
    final String toolName = functionDefinition.getName();

    final Class<P> parametersClass = functionDefinition.getParametersClass();
    final JsonNode parametersSchemaNode = ToolUtility.extractParametersSchemaNode(parametersClass);

    final McpSchema.ToolAnnotations annotations =
        new McpSchema.ToolAnnotations(
            toolName,
            // readOnlyHint
            true,
            // destructiveHint
            false,
            // idempotentHint
            false,
            // openWorldHint
            true,
            // returnDirect
            false);

    final McpSchema.Tool tool =
        McpSchema.Tool.builder()
            .name(toolName)
            .description(functionDefinition.getDescription())
            .inputSchema(McpJsonMapper.createDefault(), parametersSchemaNode.toString())
            .annotations(annotations)
            .build();
    return tool;
  }
}
