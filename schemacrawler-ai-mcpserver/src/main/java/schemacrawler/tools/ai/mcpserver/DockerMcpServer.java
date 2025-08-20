/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver;

/**
 * Construct SchemaCrawler arguments from environment variables and run SchemaCrawler MCP Server.
 */
public class DockerMcpServer {

  /**
   * Main method that reads environment variables, constructs arguments, and runs SchemaCrawler MCP
   * Server.
   *
   * @param args Command line arguments (will be combined with environment variable arguments)
   * @throws Exception If an error occurs during execution
   */
  public static void main(final String[] args) throws Exception {
    final McpServerContext context = new McpServerContext();
    final String[] arguments = context.buildArguments();
    schemacrawler.Main.main(arguments);
  }
}
