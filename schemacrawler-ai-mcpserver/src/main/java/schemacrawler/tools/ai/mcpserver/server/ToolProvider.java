/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

import static schemacrawler.tools.ai.mcpserver.utility.LoggingUtility.log;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpTool.McpAnnotations;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.Implementation;
import schemacrawler.Version;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.tools.FunctionDefinitionRegistry;
import schemacrawler.tools.ai.tools.FunctionReturnType;
import schemacrawler.tools.ai.tools.ToolSpecification;
import schemacrawler.tools.ai.utility.JsonUtility;
import us.fatehi.utility.string.StringFormat;

/**
 * Service class for managing tool providers. This class separates tool-related functionality to
 * avoid circular dependencies.
 */
@Service
public class ToolProvider {

  private static final Logger LOGGER = Logger.getLogger(ToolProvider.class.getCanonicalName());

  @Autowired
  private ServerHealth serverHealth;
  @Autowired
  private Catalog catalog;
  @Autowired
  private FunctionDefinitionRegistry functionDefinitionRegistry;

  @McpTool(name = "mcp-server-health",
      description = "Gets the SchemaCrawler MCP version and uptime status",
      annotations = @McpAnnotations(readOnlyHint = true, destructiveHint = false))
  public String getSchemaCrawlerVersion(final McpSyncServerExchange exchange,
      @McpArg(description = "MCP Client identification, if available.",
          required = false) final String clientId,
      @McpArg(description = "Event id, if available.", required = false) final String eventId) {
    final ObjectNode objectNode = JsonUtility.mapper.createObjectNode();
    objectNode.put("schemacrawler-version", Version.version().toString());
    objectNode.putPOJO("mcp-server-health", serverHealth.currentState());

    final ObjectNode clientNode = objectNode.putObject("client");
    clientNode.put("mcp-client-id", clientId);
    clientNode.put("mcp-event-id", eventId);

    if (exchange != null) {
      final Implementation clientInfo = exchange.getClientInfo();
      if (clientInfo != null) {
        clientNode.putPOJO("client-info", clientInfo);
      }
      log(exchange, objectNode.toPrettyString());
    }

    return objectNode.toString();
  }

  /**
   * Creates a tool callback provider for SchemaCrawler tools.
   *
   * @return A tool callback provider
   */
  @Bean
  public ToolCallbackProvider schemaCrawlerTools() {
    final List<ToolCallback> toolCallbacks = new ArrayList<>();
    for (final ToolSpecification toolSpecification : functionDefinitionRegistry
        .getToolSpecifications(FunctionReturnType.JSON)) {
      LOGGER.log(Level.FINE, new StringFormat("Add callback for <%s>", toolSpecification.name()));
      toolCallbacks.add(new SchemaCrawlerToolCallback(toolSpecification, catalog));
    }
    return ToolCallbackProvider.from(toolCallbacks);
  }
}
