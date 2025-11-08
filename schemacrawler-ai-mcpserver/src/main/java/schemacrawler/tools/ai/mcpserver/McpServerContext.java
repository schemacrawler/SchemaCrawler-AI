/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.util.Collection;
import java.util.Set;
import us.fatehi.utility.CollectionsUtility;
import us.fatehi.utility.ioresource.EnvironmentVariableConfig;
import us.fatehi.utility.ioresource.StringValueConfig;

/** Inner class that handles the MCP server setup. */
public final class McpServerContext {

  private static final String EXCLUDE_TOOLS = "SCHCRWLR_EXCLUDE_TOOLS";
  private static final String MCP_SERVER_TRANSPORT = "SCHCRWLR_MCP_SERVER_TRANSPORT";

  private final StringValueConfig envMap;
  private final McpServerTransportType transport;
  private final Collection<String> excludeTools;

  /** Default constructor that uses System.getenv */
  public McpServerContext() {
    this((EnvironmentVariableConfig) System::getenv);
  }

  /**
   * Constructor with environment variable accessor for testing
   *
   * @param envMap The environment variable accessor
   */
  public McpServerContext(final StringValueConfig envMap) {
    this.envMap = requireNonNull(envMap, "No environment accessor provided");
    transport = readTransport();
    excludeTools = readExcludeTools();
  }

  public Collection<String> excludeTools() {
    return excludeTools;
  }

  public McpServerTransportType mcpTransport() {
    return transport;
  }

  Collection<String> readExcludeTools() {
    return Set.of(CollectionsUtility.splitList(envMap.getStringValue(EXCLUDE_TOOLS, "")));
  }

  /**
   * Parses a string and returns a valid transport.
   *
   * @param value The transport string to check
   * @return McpServerTransportType Non-null value
   */
  McpServerTransportType readTransport() {
    final McpServerTransportType defaultValue = McpServerTransportType.stdio;

    final String value = envMap.getStringValue(MCP_SERVER_TRANSPORT, "");
    if (isBlank(value)) {
      return defaultValue;
    }
    try {
      McpServerTransportType transport = McpServerTransportType.valueOf(value);
      if (transport == McpServerTransportType.unknown) {
        transport = defaultValue;
      }
      return transport;
    } catch (final Exception e) {
      return defaultValue;
    }
  }
}
