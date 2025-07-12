/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */


package schemacrawler.tools.ai.mcpserver;

import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.tools.command.mcpserver.McpTransport;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
public class McpServerUtility {

  private static final Logger LOGGER = Logger.getLogger(McpServerUtility.class.getName());

  public static void startMcpServer(final McpTransport mcpTransport) {
    if (mcpTransport == null) {
      throw new IllegalArgumentException("MCP transport not provided");
    }
    switch (mcpTransport) {
      case sse:
        SseMcpServer.start();
        break;
      case stdio:
      default:
        StdioMcpServer.start();
        break;
    }
    LOGGER.log(Level.INFO,
        new StringFormat("MCP server is running with <%s> transport", mcpTransport));
  }

  private McpServerUtility() {
    // Prevent instantiation
  }
}
