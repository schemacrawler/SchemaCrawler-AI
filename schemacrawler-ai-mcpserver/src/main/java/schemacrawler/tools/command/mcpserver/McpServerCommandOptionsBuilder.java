/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.mcpserver;

import static schemacrawler.tools.command.mcpserver.McpServerTransportType.unknown;
import schemacrawler.schemacrawler.OptionsBuilder;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigOptionsBuilder;

public final class McpServerCommandOptionsBuilder
    implements OptionsBuilder<McpServerCommandOptionsBuilder, McpServerCommandOptions>,
        ConfigOptionsBuilder<McpServerCommandOptionsBuilder, McpServerCommandOptions> {

  public static McpServerCommandOptionsBuilder builder() {
    return new McpServerCommandOptionsBuilder();
  }

  private McpServerTransportType mcpTransport;

  private McpServerCommandOptionsBuilder() {
    // MCP Server transport needs to be explicitly specified,
    // so default to known
    mcpTransport = unknown;
  }

  @Override
  public McpServerCommandOptionsBuilder fromConfig(final Config config) {
    if (config != null) {
      mcpTransport = config.getEnumValue("transport", unknown);
    }

    return this;
  }

  @Override
  public McpServerCommandOptionsBuilder fromOptions(final McpServerCommandOptions options) {
    if (options != null) {
      mcpTransport = options.mcpTransport();
    }
    return this;
  }

  @Override
  public Config toConfig() {
    throw new UnsupportedOperationException("Cannot load transport from config file");
  }

  @Override
  public McpServerCommandOptions toOptions() {
    return new McpServerCommandOptions(mcpTransport);
  }

  public McpServerCommandOptionsBuilder withMcpTransport(
      final McpServerTransportType mcpServerType) {
    if (mcpServerType != null) {
      mcpTransport = mcpServerType;
    }
    return this;
  }
}
