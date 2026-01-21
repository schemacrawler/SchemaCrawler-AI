/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.command.mcpserver;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.tools.ai.mcpserver.McpServerMain;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;
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
    final Collection<String> excludeTools = commandOptions.excludeTools();
    McpServerMain.startMcpServer(catalog, connection, mcpTransport, excludeTools);
  }

  @Override
  public boolean usesConnection() {
    // Support commands that use connections
    return true;
  }
}
