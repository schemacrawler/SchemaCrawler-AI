/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.server;

import static schemacrawler.tools.ai.mcpserver.server.CallToolLogger.TurnType.RESPONSE;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.ai.mcp.annotation.McpArg;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import schemacrawler.loader.catalog.summary.CatalogStats.SchemaStats;
import schemacrawler.loader.catalog.summary.CatalogStatsUtility;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schemacrawler.Version;
import schemacrawler.tools.ai.mcpserver.ExcludeTools;
import schemacrawler.tools.ai.mcpserver.utility.DatabaseIdentityUtility;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionDefinitionRegistry;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import us.fatehi.utility.property.Property;
import us.fatehi.utility.string.StringFormat;

/**
 * Service class for managing tool providers. This class separates tool-related functionality to
 * avoid circular dependencies.
 */
@Service
public class ToolProvider {

  private static final Logger LOGGER = Logger.getLogger(ToolProvider.class.getCanonicalName());

  @Autowired private ServerHealth serverHealth;
  @Autowired private Catalog catalog;
  @Autowired private DatabaseIdentity databaseIdentity;
  @Autowired private FunctionDefinitionRegistry functionDefinitionRegistry;
  @Autowired private ToolHelper toolHelper;
  @Autowired private ExcludeTools excludeTools;
  @Autowired private boolean isOffline;

  @McpTool(
      name = "mcp-server-health",
      title = "Show SchemaCrawler AI MCP Server health",
      description = "Gets the SchemaCrawler AI MCP Server version and uptime status.",
      annotations =
          @McpTool.McpAnnotations(
              title = "SchemaCrawler AI MCP Server Health",
              readOnlyHint = true,
              destructiveHint = false,
              idempotentHint = false, // Health can change based on server uptime
              openWorldHint = false))
  public JsonNode getSchemaCrawlerVersion(
      final McpSyncServerExchange exchange,
      @McpArg(description = "MCP Client identification, if available.", required = false)
          final String clientId,
      @McpArg(description = "Event id, if available.", required = false) final String eventId) {
    final ObjectNode objectNode = mapper.createObjectNode();
    objectNode.put("schemacrawler-version", Version.version().toString());
    objectNode.putPOJO("mcp-server-health", serverHealth.currentState());

    final ObjectNode clientNode = objectNode.putObject("client");
    clientNode.put("mcp-client-id", clientId);
    clientNode.put("mcp-event-id", eventId);

    // Log execution
    final CallToolLogger logger = new CallToolLogger(exchange);
    logger.setFunctionCallbackNode(clientNode);
    logger.log(RESPONSE, "Returned SchemaCrawler AI MCP Server health");

    return objectNode;
  }

  @McpTool(
      name = "about_database",
      title = "Show database server information",
      description =
          "Provides database environment and server configuration metadata, including engine "
              + "type and version, collation, encoding, parameters, capabilities, and platform "
              + "details. Also reports database identity, including alias, product, and server "
              + "fingerprint.",
      annotations =
          @McpTool.McpAnnotations(
              title = "Show database server information",
              readOnlyHint = true,
              destructiveHint = false,
              idempotentHint = true,
              openWorldHint = false))
  public JsonNode aboutDatabase(final McpSyncServerExchange exchange) {

    final DatabaseInfo databaseInfo = catalog.getDatabaseInfo();
    final ObjectNode aboutDatabase = mapper.createObjectNode();

    final ObjectNode databaseServer = DatabaseIdentityUtility.toDetailNode(databaseIdentity);
    aboutDatabase.set("database_server", databaseServer);

    final ArrayNode serverInfoArray = aboutDatabase.putArray("server_info");
    final Collection<Property> serverInfo = databaseInfo.getServerInfo();
    for (final Property serverProperty : serverInfo) {
      if (serverProperty == null || serverProperty.getValue() == null) {
        continue;
      }
      final ObjectNode serverPropertyNode = serverInfoArray.addObject();
      serverPropertyNode.put("name", serverProperty.getName());
      serverPropertyNode.put("description", serverProperty.getDescription());
      serverPropertyNode.put("value", serverProperty.getValue().toString());
    }

    final List<SchemaStats> schemaStats = CatalogStatsUtility.schemaStatsFrom(catalog);
    if (schemaStats != null && !schemaStats.isEmpty()) {
      aboutDatabase.set("schemas", mapper.valueToTree(schemaStats));
    }

    // Log execution
    final CallToolLogger logger = new CallToolLogger(exchange);
    logger.setFunctionCallbackNode(mapper.createObjectNode().put("name", "about_database"));
    logger.log(RESPONSE, "Returned %s".formatted(databaseInfo.getDatabaseProductName()));

    return aboutDatabase;
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
        LOGGER.log(
            Level.WARNING,
            new StringFormat("Excluding tool <%s> since it was on the exclude list", functionName));
        continue;
      }
      if (isOffline && functionDefinition.usesConnection()) {
        LOGGER.log(
            Level.WARNING,
            new StringFormat("Excluding tool <%s> since it uses a live connection", functionName));
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
