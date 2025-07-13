/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */



package schemacrawler.tools.command.mcpserver;

import schemacrawler.schemacrawler.OptionsBuilder;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigOptionsBuilder;

public final class McpServerCommandOptionsBuilder
    implements OptionsBuilder<McpServerCommandOptionsBuilder, McpServerCommandOptions>,
        ConfigOptionsBuilder<McpServerCommandOptionsBuilder, McpServerCommandOptions> {

  public static McpServerCommandOptionsBuilder builder() {
    return new McpServerCommandOptionsBuilder();
  }

  private McpTransport mcpTransport;

  private McpServerCommandOptionsBuilder() {
    mcpTransport = McpTransport.stdio;
  }

  @Override
  public McpServerCommandOptionsBuilder fromOptions(final McpServerCommandOptions options) {
    if (options != null) {
      mcpTransport = options.mcpTransport();
    }
    return this;
  }

  public McpServerCommandOptionsBuilder withMcpTransport(McpTransport mcpServerType) {
    if (mcpServerType != null) {
      mcpTransport = mcpServerType;
    }
    return this;
  }

  @Override
  public McpServerCommandOptions toOptions() {
    return new McpServerCommandOptions(mcpTransport);
  }

  @Override
  public McpServerCommandOptionsBuilder fromConfig(final Config config) {
    if (config != null) {
      mcpTransport = config.getEnumValue("transport", mcpTransport.stdio);
    }

    return this;
  }

  @Override
  public Config toConfig() {
    throw new UnsupportedOperationException("Cannot load transport from config file");
  }
}
