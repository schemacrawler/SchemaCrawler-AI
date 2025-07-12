/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */


package schemacrawler.tools.command.aichat.mcp.command;

import static schemacrawler.tools.command.aichat.mcp.McpServerUtility.startMcpServer;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.tools.command.aichat.mcp.server.ConfigurationManager;
import schemacrawler.tools.command.aichat.mcp.server.ConnectionService;
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
    ConnectionService.instantiate(commandOptions, catalog, connection);
    ConfigurationManager.getInstance().setDryRun(false);
    startMcpServer(commandOptions.mcpTransport());
  }

  @Override
  public boolean usesConnection() {
    // Support commands that use connections
    return true;
  }
}
