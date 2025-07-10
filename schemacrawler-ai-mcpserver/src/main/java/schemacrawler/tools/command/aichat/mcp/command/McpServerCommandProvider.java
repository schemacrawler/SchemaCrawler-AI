/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.aichat.mcp.command;

import static schemacrawler.tools.command.aichat.mcp.command.McpServerCommand.COMMAND;
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
