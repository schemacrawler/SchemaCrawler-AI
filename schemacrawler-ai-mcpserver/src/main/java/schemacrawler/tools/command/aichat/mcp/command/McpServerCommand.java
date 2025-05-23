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

import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.tools.command.aichat.mcp.server.ConfigurationManager;
import schemacrawler.tools.command.aichat.mcp.server.ConnectionService;
import schemacrawler.tools.command.aichat.mcp.SchemaCrawlerMcpServer;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import us.fatehi.utility.property.PropertyName;

/** SchemaCrawler command plug-in. */
public final class McpServerCommand extends BaseSchemaCrawlerCommand<McpServerCommandOptions> {

  private static final Logger LOGGER = Logger.getLogger(McpServerCommand.class.getName());

  static final PropertyName COMMAND =
      new PropertyName("mcpserver", "Allow AI agents access to your schema");

  protected McpServerCommand() {
    super(COMMAND);
  }

  @Override
  public void checkAvailability() throws RuntimeException {
    LOGGER.log(Level.FINE, "No checks required for MCP server");
  }

  @Override
  public void execute() {
    LOGGER.log(Level.INFO, "Starting MCP server");
    ConnectionService.instantiate(commandOptions, catalog, connection);
    ConfigurationManager.getInstance().setDryRun(false);
    SchemaCrawlerMcpServer.start();
    LOGGER.log(Level.INFO, "MCP server is running");
  }

  @Override
  public boolean usesConnection() {
    // Support commands that use connections
    return true;
  }
}
