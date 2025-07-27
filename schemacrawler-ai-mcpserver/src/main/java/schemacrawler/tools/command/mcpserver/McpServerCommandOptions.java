/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.mcpserver;

import schemacrawler.tools.executable.CommandOptions;

public record McpServerCommandOptions(McpServerTransportType mcpTransport)
    implements CommandOptions {

  public McpServerCommandOptions {
    if (mcpTransport == null || mcpTransport == McpServerTransportType.unknown) {
      throw new IllegalArgumentException("No MCP Server transport specified");
    }
  }
}
