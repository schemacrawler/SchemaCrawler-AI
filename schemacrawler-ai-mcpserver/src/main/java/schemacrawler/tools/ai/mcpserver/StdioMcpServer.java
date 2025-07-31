/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application for the SchemaCrawler AI MCP server. This class enables the Spring AI MCP
 * server capabilities.
 */
@SpringBootApplication
public class StdioMcpServer {

  public static void main(final String[] args) {
    start();
  }

  public static void start() {
    final SpringApplication app = new SpringApplication(StdioMcpServer.class);
    app.setAdditionalProfiles("stdio");
    app.run();
  }
}
