/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.server;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.mcpserver.utility.LoggingUtility.log;
import static schemacrawler.tools.ai.mcpserver.utility.LoggingUtility.logExceptionToClient;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapperSupplier;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import java.util.List;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.ai.tools.ExceptionFunctionReturn;
import schemacrawler.tools.ai.tools.FunctionCallback;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.FunctionReturn;
import tools.jackson.databind.JsonNode;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

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
        log(exchange, "Executing", functionCallback.toCallObject(arguments));
        final DatabaseConnectionSource connectionSource =
            DatabaseConnectionService.getDatabaseConnectionSource();
        functionReturn = functionCallback.execute(arguments, connectionSource);
      } catch (final Exception e) {
        logExceptionToClient(
            exchange,
            functionCallback.getFunctionName().getName() + ":\n" + request.arguments(),
            e);
        functionReturn = new ExceptionFunctionReturn(e);
      }
      final List<Content> content = createContent(functionReturn);
      final boolean inError = functionReturn instanceof ExceptionFunctionReturn;
      final CallToolResult callToolResult =
          CallToolResult.builder().content(content).isError(inError).build();
      return callToolResult;
    }

    private List<Content> createContent(final FunctionReturn functionReturn) {
      final Content content;
      if (functionReturn == null) {
        content = new TextContent("");
      } else {
        content = new TextContent(functionReturn.get());
      }
      return List.of(content);
    }
  }

  private static final Logger LOGGER = Logger.getLogger(ToolHelper.class.getCanonicalName());

  @Autowired private Catalog catalog;
  @Autowired private ERModel erModel;

  public <P extends FunctionParameters>
      McpServerFeatures.SyncToolSpecification toSyncToolSpecification(
          final FunctionDefinition<P> functionDefinition) {

    final McpSchema.Tool tool = toTool(functionDefinition);
    final FunctionCallback<P> functionCallback =
        new FunctionCallback<>(functionDefinition, catalog, erModel);
    final ToolCallHandler toolCallHandler = new ToolCallHandler(functionCallback);

    return new McpServerFeatures.SyncToolSpecification(tool, toolCallHandler);
  }

  private <P extends FunctionParameters> Tool toTool(
      final FunctionDefinition<P> functionDefinition) {
    requireNonNull(functionDefinition, "No function definition provided");
    final String toolName = functionDefinition.getName();
    final JsonNode definitionNode = functionDefinition.toJson();
    if (definitionNode == null || !definitionNode.has("inputSchema")) {
      throw new InternalRuntimeException("Bad JSON node for <%s>".formatted(functionDefinition));
    }
    final JsonNode inputSchemaNode = definitionNode.get("inputSchema");
    if (LOGGER.isLoggable(Level.INFO)) {
      LOGGER.log(
          Level.INFO, "Preparing to register tool:%n%s".formatted(definitionNode.toPrettyString()));
    }

    final McpSchema.Tool tool =
        McpSchema.Tool.builder()
            .name(toolName)
            .title(functionDefinition.getTitle())
            .description(functionDefinition.getDescription())
            .inputSchema(new JacksonMcpJsonMapperSupplier().get(), inputSchemaNode.toString())
            .build();
    return tool;
  }
}
