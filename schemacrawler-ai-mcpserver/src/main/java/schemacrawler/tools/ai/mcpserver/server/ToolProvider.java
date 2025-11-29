/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

import static schemacrawler.tools.ai.mcpserver.utility.LoggingUtility.log;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.Implementation;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpTool.McpAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import schemacrawler.Version;
import schemacrawler.tools.ai.mcpserver.ExcludeTools;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionDefinitionRegistry;
import schemacrawler.tools.ai.utility.JsonUtility;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import us.fatehi.utility.string.StringFormat;

/**
 * Service class for managing tool providers. This class separates tool-related functionality to
 * avoid circular dependencies.
 */
@Service
public class ToolProvider {

  private static final Logger LOGGER = Logger.getLogger(ToolProvider.class.getCanonicalName());

  @Autowired private ServerHealth serverHealth;
  @Autowired private FunctionDefinitionRegistry functionDefinitionRegistry;
  @Autowired private ToolHelper toolHelper;
  @Autowired private ExcludeTools excludeTools;

  @McpTool(
      name = "mcp-server-health",
      title = "Show SchemaCrawler AI MCP Server health",
      description = "Gets the SchemaCrawler AI MCP Server version and uptime status.",
      annotations = @McpAnnotations(readOnlyHint = true, destructiveHint = false))
  public JsonNode getSchemaCrawlerVersion(
      final McpSyncServerExchange exchange,
      @McpArg(description = "MCP Client identification, if available.", required = false)
          final String clientId,
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
      log(exchange, "MCP Server Health", objectNode);
    }

    return objectNode;
  }

  /**
   * Creates tool callbacks for SchemaCrawler tools.
   *
   * @return Registers tool callbacks
   */
  @Bean
  public List<McpServerFeatures.SyncToolSpecification> schemaCrawlerTools() {
    final List<McpServerFeatures.SyncToolSpecification> tools = new ArrayList<>();
    for (final FunctionDefinition<?> functionDefinition :
        functionDefinitionRegistry.getFunctionDefinitions()) {
      final String functionName = functionDefinition.getFunctionName().getName();
      if (excludeTools == null || excludeTools.excludeTools().contains(functionName)) {
        LOGGER.log(Level.WARNING, new StringFormat("Excluding tool <%s>", functionName));
        continue;
      }
      LOGGER.log(Level.INFO, new StringFormat("Adding tool specification <%s>", functionName));
      LOGGER.log(Level.FINE, new StringFormat("%s", functionDefinition));

      final McpServerFeatures.SyncToolSpecification toolSpecification =
          toolHelper.toSyncToolSpecification(functionDefinition);
      tools.add(toolSpecification);
    }
    return tools;
  }
}
