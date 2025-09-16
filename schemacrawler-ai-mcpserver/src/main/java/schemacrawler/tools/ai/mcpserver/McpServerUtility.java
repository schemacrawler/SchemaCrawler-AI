/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver;

import static java.util.Objects.requireNonNull;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import schemacrawler.tools.command.mcpserver.McpServerTransportType;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
public class McpServerUtility {

  private static final Logger LOGGER = Logger.getLogger(McpServerUtility.class.getName());

  @SpringBootApplication
  public static class McpServer {}

  public static void startMcpServer(final McpServerTransportType mcpTransport) {
    requireNonNull(mcpTransport, "No MCP transport specified");
    new SpringApplicationBuilder(McpServer.class).profiles(mcpTransport.name()).run();
    LOGGER.log(
        Level.INFO, new StringFormat("MCP server is running with <%s> transport", mcpTransport));
  }

  private McpServerUtility() {
    // Prevent instantiation
  }
}
