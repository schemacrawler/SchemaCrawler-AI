/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver;

import java.sql.Connection;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import schemacrawler.schema.Catalog;
import us.fatehi.utility.string.StringFormat;

/**
 * Construct SchemaCrawler arguments from environment variables and run SchemaCrawler MCP Server.
 */
public class McpServerMain {

  @SpringBootApplication
  public static class McpServer {}

  private static final Logger LOGGER = Logger.getLogger(McpServerMain.class.getName());

  /**
   * Main method that reads environment variables, constructs arguments, and runs SchemaCrawler MCP
   * Server.
   *
   * @param args Command line arguments (will be combined with environment variable arguments)
   * @throws Exception If an error occurs during execution
   */
  public static void main(final String[] args) throws Exception {
    McpServerMain.startMcpServer();
  }

  public static void startMcpServer() {
    final SchemaCrawlerContext scContext = new SchemaCrawlerContext();
    final McpServerContext context = new McpServerContext();
    final McpServerTransportType mcpTransport = context.mcpTransport();
    new SpringApplicationBuilder(McpServer.class)
        .initializers(new McpServerInitializer(scContext, context))
        .profiles(mcpTransport.name())
        .run();
    LOGGER.log(
        Level.INFO, new StringFormat("MCP server is running with <%s> transport", mcpTransport));
  }

  public static void startMcpServer(
      final Catalog catalog,
      final Connection connection,
      final McpServerTransportType mcpTransport,
      final Collection<String> excludeTools) {
    new SpringApplicationBuilder(McpServer.class)
        .initializers(new McpServerInitializer(catalog, connection, mcpTransport, excludeTools))
        .profiles(mcpTransport.name())
        .run();
    LOGGER.log(
        Level.INFO, new StringFormat("MCP server is running with <%s> transport", mcpTransport));
  }
}
