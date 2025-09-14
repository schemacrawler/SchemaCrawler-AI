/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.mcpserver.McpServerUtility.startMcpServer;

import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.ai.mcpserver.server.ConfigurationManager;
import schemacrawler.tools.ai.mcpserver.server.ConnectionService;
import schemacrawler.tools.command.mcpserver.McpServerTransportType;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

/**
 * Construct SchemaCrawler arguments from environment variables and run SchemaCrawler MCP Server.
 */
public class McpServerMain {

  private static final Logger LOGGER = Logger.getLogger(McpServerMain.class.getName());

  /**
   * Main method that reads environment variables, constructs arguments, and runs SchemaCrawler MCP
   * Server.
   *
   * @param args Command line arguments (will be combined with environment variable arguments)
   * @throws Exception If an error occurs during execution
   */
  public static void main(final String[] args) throws Exception {
    // Read options from environmental variable
    final McpServerContext context = new McpServerContext();
    final Catalog catalog = getCatalog(context);
    ConfigurationManager.instantiate(catalog);
    // Start the MCP server
    startMcpServer(context.mcpTransport());
  }

  private static Catalog getCatalog(final McpServerContext context) {
    requireNonNull(context, "No context provided");
    try {
      final SchemaCrawlerOptions schemaCrawlerOptions = context.getSchemaCrawlerOptions();
      final DatabaseConnectionSource connectionSource =
          context.buildCatalogDatabaseConnectionSource();
      // Obtain the database catalog
      final Catalog catalog =
          SchemaCrawlerUtility.getCatalog(connectionSource, schemaCrawlerOptions);
      ConnectionService.instantiate(connectionSource);
      return catalog;
    } catch (final Exception e) {
      LOGGER.log(Level.SEVERE, "Could not load catalog", e);
      if (context.mcpTransport() != McpServerTransportType.stdio) {
        throw new SchemaCrawlerException("Could not obtain database metadata", e);
      }
      LOGGER.log(Level.SEVERE, "Server is running in an error state");
      if (!ConnectionService.isInstantiated()) {
        ConnectionService.instantiate(new EmptyDatabaseConnectionSource());
      }
      return new EmptyCatalog(e);
    }
  }
}
