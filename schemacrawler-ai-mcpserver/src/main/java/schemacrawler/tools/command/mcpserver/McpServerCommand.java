/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.mcpserver;

import static schemacrawler.tools.ai.mcpserver.McpServerUtility.startMcpServer;

import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.tools.ai.mcpserver.server.ConfigurationManager;
import schemacrawler.tools.ai.mcpserver.server.ConnectionService;
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
    final McpServerTransportType mcpTransport = commandOptions.mcpTransport();
    ConnectionService.instantiate(connection);
    ConfigurationManager.instantiate(mcpTransport, catalog);
    startMcpServer(mcpTransport);
  }

  @Override
  public boolean usesConnection() {
    // Support commands that use connections
    return true;
  }
}
