/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */



package schemacrawler.tools.command.mcpserver;

import static schemacrawler.tools.command.mcpserver.McpServerCommand.COMMAND;
import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;

/** SchemaCrawler command plug-in for AI chat. */
public final class McpServerCommandProvider extends BaseCommandProvider {

  public McpServerCommandProvider() {
    super(COMMAND);
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    final PluginCommand pluginCommand = newPluginCommand(COMMAND);
    pluginCommand.addOption("transport", McpTransport.class, "Type of MCP server to start");
    return pluginCommand;
  }

  @Override
  public McpServerCommand newSchemaCrawlerCommand(final String command, final Config config) {
    if (!supportsCommand(command)) {
      throw new IllegalArgumentException("Cannot support command, " + command);
    }

    try {
      final McpServerCommandOptions options =
          McpServerCommandOptionsBuilder.builder().fromConfig(config).toOptions();

      final McpServerCommand scCommand = new McpServerCommand();
      scCommand.configure(options);
      return scCommand;
    } catch (final Exception e) {
      throw new ExecutionRuntimeException(e);
    }
  }

  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    return true;
  }
}
