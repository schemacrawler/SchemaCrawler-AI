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

import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapperSupplier;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.Annotations;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import io.modelcontextprotocol.spec.McpSchema.ToolAnnotations;
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
import schemacrawler.tools.ai.tools.FunctionReturnMetadata;
import schemacrawler.tools.ai.tools.TextFunctionReturn;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
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
      return CallToolResult.builder().content(content).isError(inError).build();
    }

    private List<Content> createContent(final FunctionReturn functionReturn) {

      final FunctionReturn result = requireNonNullElse(functionReturn, new TextFunctionReturn(""));
      final FunctionReturnMetadata resultMetadata = result.getMetadata();

      final Content toolOutput =
          TextContent.builder(result.get())
              .meta(resultMetadata.toMetadataMap("schemacrawler-ai/"))
              .build();
      // Repeat the metadata as a content item for clients that do not read the meta
      // field
      final Content metadataContent = toolResultMetadataContent(resultMetadata);

      return List.of(toolOutput, metadataContent);
    }

    private Content toolResultMetadataContent(final FunctionReturnMetadata resultMetadata) {
      final Annotations annotations =
          Annotations.builder().audience(List.of(Role.ASSISTANT)).priority(0.7).build();
      final Content metadataContent =
          TextContent.builder(NO_INDENT_MAPPER.writeValueAsString(resultMetadata.toMetadataMap()))
              .annotations(annotations)
              .build();
      return metadataContent;
    }
  }

  private static final Logger LOGGER = Logger.getLogger(ToolHelper.class.getCanonicalName());

  private static final ObjectMapper NO_INDENT_MAPPER =
      mapper.rebuild().disable(INDENT_OUTPUT).build();

  @Autowired private Catalog catalog;
  @Autowired private ERModel erModel;

  public <P extends FunctionParameters>
      McpServerFeatures.SyncToolSpecification toSyncToolSpecification(
          final FunctionDefinition<P> functionDefinition) {

    final Tool tool = toTool(functionDefinition);
    final FunctionCallback<P> functionCallback =
        new FunctionCallback<>(functionDefinition, catalog, erModel);
    final ToolCallHandler toolCallHandler = new ToolCallHandler(functionCallback);

    return new McpServerFeatures.SyncToolSpecification(tool, toolCallHandler);
  }

  private <P extends FunctionParameters> Tool toTool(
      final FunctionDefinition<P> functionDefinition) {
    requireNonNull(functionDefinition, "No function definition provided");

    final String toolName = functionDefinition.getName();
    final String title = functionDefinition.getTitle();
    final boolean isIdempotent = functionDefinition.isIdempotent();

    final JsonNode definitionNode = functionDefinition.toJson();
    if (definitionNode == null || !definitionNode.has("inputSchema")) {
      throw new InternalRuntimeException("Bad JSON node for <%s>".formatted(functionDefinition));
    }
    final JsonNode inputSchemaNode = definitionNode.get("inputSchema");
    if (LOGGER.isLoggable(Level.INFO)) {
      LOGGER.log(
          Level.INFO, "Preparing to register tool:%n%s".formatted(definitionNode.toPrettyString()));
    }
    final String inputSchema = inputSchemaNode.toString();

    final McpJsonMapper jsonMapper = new JacksonMcpJsonMapperSupplier().get();

    final ToolAnnotations toolAnnotations =
        ToolAnnotations.builder()
            .title(title)
            .readOnlyHint(true)
            .destructiveHint(false)
            .idempotentHint(isIdempotent)
            .openWorldHint(false)
            .returnDirect(false)
            .build();

    final Tool tool =
        Tool.builder(toolName, jsonMapper, inputSchema)
            .title(title)
            .description(functionDefinition.getDescription())
            .annotations(toolAnnotations)
            .build();
    return tool;
  }
}
