/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver;

public enum McpServerTransportType {
  unknown("unknown"),
  stdio("\"stdio\""),
  http("Streamable HTTP");

  private final String description;

  McpServerTransportType(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
