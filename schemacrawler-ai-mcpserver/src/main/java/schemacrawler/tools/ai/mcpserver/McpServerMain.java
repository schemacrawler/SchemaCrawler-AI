/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver;

import static schemacrawler.tools.ai.mcpserver.McpServerUtility.startMcpServer;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.ai.mcpserver.server.ConfigurationManager;
import schemacrawler.tools.ai.mcpserver.server.ConnectionService;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

/**
 * Construct SchemaCrawler arguments from environment variables and run SchemaCrawler MCP Server.
 */
public class McpServerMain {

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
    final SchemaCrawlerOptions schemaCrawlerOptions = context.getSchemaCrawlerOptions();
    final DatabaseConnectionSource connectionSource =
        context.buildCatalogDatabaseConnectionSource();
    // Obtain the database catalog
    final Catalog catalog = SchemaCrawlerUtility.getCatalog(connectionSource, schemaCrawlerOptions);
    ConnectionService.instantiate(connectionSource);
    ConfigurationManager.instantiate(catalog);
    // Start the MCP server
    startMcpServer(context.mcpTransport());
  }
}
