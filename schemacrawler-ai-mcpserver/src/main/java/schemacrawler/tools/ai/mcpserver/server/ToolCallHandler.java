/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.server;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;
import static schemacrawler.tools.ai.mcpserver.utility.LoggingUtility.log;
import static schemacrawler.tools.ai.mcpserver.utility.LoggingUtility.logExceptionToClient;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.Annotations;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import java.util.List;
import java.util.function.BiFunction;
import schemacrawler.tools.ai.tools.ExceptionFunctionReturn;
import schemacrawler.tools.ai.tools.FunctionCallback;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.TextFunctionReturn;
import tools.jackson.databind.ObjectMapper;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

class ToolCallHandler
    implements BiFunction<McpSyncServerExchange, CallToolRequest, CallToolResult> {

  private static final ObjectMapper NO_INDENT_MAPPER =
      mapper.rebuild().disable(INDENT_OUTPUT).build();

  private final FunctionCallback<? extends FunctionParameters> functionCallback;

  ToolCallHandler(final FunctionCallback<? extends FunctionParameters> functionCallback) {
    this.functionCallback = requireNonNull(functionCallback, "No function callback provided");
  }

  @Override
  public CallToolResult apply(final McpSyncServerExchange exchange, final CallToolRequest request) {
    FunctionReturn functionReturn;
    try {
      final String arguments = mapper.writeValueAsString(request.arguments());
      log(exchange, "Executing", functionCallback.toCallObject(arguments));
      final DatabaseConnectionSource connectionSource =
          DatabaseConnectionService.getDatabaseConnectionSource();
      functionReturn = functionCallback.execute(arguments, connectionSource);
    } catch (final Exception e) {
      logExceptionToClient(
          exchange, functionCallback.getFunctionName().getName() + ":\n" + request.arguments(), e);
      functionReturn = new ExceptionFunctionReturn(e);
    }
    final List<Content> content = createContent(functionReturn);
    final boolean inError = functionReturn instanceof ExceptionFunctionReturn;
    return CallToolResult.builder().content(content).isError(inError).build();
  }

  private List<Content> createContent(final FunctionReturn functionReturn) {
    final FunctionReturn result = requireNonNullElse(functionReturn, new TextFunctionReturn(""));
    return List.of(toolOutputContent(result), toolOutputMetadataContent(result));
  }

  /** Create content from the tool result. */
  private Content toolOutputContent(final FunctionReturn result) {
    final Content toolOutput =
        TextContent.builder(result.get())
            .meta(result.getMetadata().toMetadataMap("schemacrawler-ai/"))
            .build();
    return toolOutput;
  }

  /** Repeat the metadata as a content item for clients that do not read the meta field. */
  private Content toolOutputMetadataContent(final FunctionReturn result) {
    final Annotations annotations =
        Annotations.builder().audience(List.of(Role.ASSISTANT)).priority(0.7).build();
    final Content metadataContent =
        TextContent.builder(
                NO_INDENT_MAPPER.writeValueAsString(result.getMetadata().toMetadataMap()))
            .annotations(annotations)
            .build();
    return metadataContent;
  }
}
