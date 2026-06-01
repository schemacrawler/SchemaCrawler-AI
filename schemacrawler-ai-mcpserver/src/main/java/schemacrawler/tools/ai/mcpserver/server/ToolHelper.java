/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.server;

import static java.util.Objects.requireNonNull;

import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapperSupplier;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import io.modelcontextprotocol.spec.McpSchema.ToolAnnotations;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.ai.tools.FunctionCallback;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionParameters;
import tools.jackson.databind.JsonNode;

@Component
public class ToolHelper {

  private static final Logger LOGGER = Logger.getLogger(ToolHelper.class.getCanonicalName());

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
