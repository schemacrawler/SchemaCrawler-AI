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

import schemacrawler.schemacrawler.OptionsBuilder;

public final class McpServerCommandOptionsBuilder
    implements OptionsBuilder<McpServerCommandOptionsBuilder, McpServerCommandOptions> {

  public static McpServerCommandOptionsBuilder builder() {
    return new McpServerCommandOptionsBuilder();
  }

  private McpServerType mcpServerType;

  private McpServerCommandOptionsBuilder() {
    mcpServerType = McpServerType.sse;
  }

  @Override
  public McpServerCommandOptionsBuilder fromOptions(final McpServerCommandOptions options) {
    if (options != null) {
      mcpServerType = options.mcpServerType();
    }
    return this;
  }

  public McpServerCommandOptionsBuilder withMcpServerType(McpServerType mcpServerType) {
    if (mcpServerType != null) {
      this.mcpServerType = mcpServerType;
    }
    return this;
  }

  @Override
  public McpServerCommandOptions toOptions() {
    return new McpServerCommandOptions(mcpServerType);
  }
}
