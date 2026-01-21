/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.command.mcpserver;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import org.jspecify.annotations.NonNull;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;
import schemacrawler.tools.executable.CommandOptions;

public record McpServerCommandOptions(
    @NonNull McpServerTransportType mcpTransport, @NonNull Collection<String> excludeTools)
    implements CommandOptions {

  public McpServerCommandOptions {
    if (mcpTransport == null || mcpTransport == McpServerTransportType.unknown) {
      throw new IllegalArgumentException("No MCP Server transport specified");
    }
    requireNonNull(excludeTools, "No exclude tools list provided");
  }
}
