/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;

@DisplayName("MCP Server configuration tests")
public class McpServerContextTest {

  private Config envAccessor;
  private McpServerContext context;

  @BeforeEach
  void setUp() {
    envAccessor = ConfigUtility.newConfig();
  }

  @Test
  @DisplayName("Should handle blanks, spaces, and duplicates in exclude tools")
  void shouldHandleBlanksSpacesAndDuplicatesInExcludeTools() {
    // Arrange
    envAccessor.put("SCHCRWLR_EXCLUDE_TOOLS", "  toolA , , toolB,toolA ,  ,ToolA  ");
    context = new McpServerContext(envAccessor);

    // Act
    final Collection<String> excluded = context.excludeTools();

    // Assert
    // "toolA" appears twice but should be included once due to Set semantics
    assertThat(excluded.contains("toolA"), is(true));
    assertThat(excluded.contains("toolB"), is(true));
    // "ToolA" (different case) is a different entry because matching is case-sensitive
    assertThat(excluded.contains("ToolA"), is(true));
    assertThat(excluded.size(), is(3));
  }

  @Test
  @DisplayName("Should parse comma-separated exclude tools list")
  void shouldParseExcludeToolsList() {
    // Arrange
    envAccessor.put("SCHCRWLR_EXCLUDE_TOOLS", "tool1,tool2,tool3");
    context = new McpServerContext(envAccessor);

    // Act
    final Collection<String> excluded = context.excludeTools();

    // Assert
    assertThat(excluded.contains("tool1"), is(true));
    assertThat(excluded.contains("tool2"), is(true));
    assertThat(excluded.contains("tool3"), is(true));
    assertThat(excluded.size(), is(3));
  }

  @Test
  @DisplayName("Should return empty set when SCHCRWLR_EXCLUDE_TOOLS is unset or empty")
  void shouldReturnEmptySetWhenExcludeToolsUnsetOrEmpty() {
    // Unset (null)
    envAccessor.put("SCHCRWLR_EXCLUDE_TOOLS", null);
    context = new McpServerContext(envAccessor);
    Collection<String> excluded = context.excludeTools();
    assertThat(excluded.size(), is(0));

    // Empty string
    envAccessor.put("SCHCRWLR_EXCLUDE_TOOLS", "");
    context = new McpServerContext(envAccessor);
    excluded = context.excludeTools();
    assertThat(excluded.size(), is(0));

    // Only commas and spaces
    envAccessor.put("SCHCRWLR_EXCLUDE_TOOLS", ", , ,  ,");
    context = new McpServerContext(envAccessor);
    excluded = context.excludeTools();
    assertThat(excluded.size(), is(0));
  }

  @Test
  @DisplayName("Should validate transport correctly")
  void shouldValidateTransport() {
    // Test stdio transport
    envAccessor.put("SCHCRWLR_MCP_SERVER_TRANSPORT", "stdio");
    context = new McpServerContext(envAccessor);
    assertThat(context.mcpTransport(), is(McpServerTransportType.stdio));

    // Test http transport
    envAccessor.put("SCHCRWLR_MCP_SERVER_TRANSPORT", "http");
    context = new McpServerContext(envAccessor);
    assertThat(context.mcpTransport(), is(McpServerTransportType.http));

    // Test unknown value defaults to stdio
    envAccessor.put("SCHCRWLR_MCP_SERVER_TRANSPORT", "unknown");
    context = new McpServerContext(envAccessor);
    assertThat(context.mcpTransport(), is(McpServerTransportType.stdio));

    // Test null defaults to stdio
    envAccessor.put("SCHCRWLR_MCP_SERVER_TRANSPORT", null);
    context = new McpServerContext(envAccessor);
    assertThat(context.mcpTransport(), is(McpServerTransportType.stdio));

    // Test empty string defaults to stdio
    envAccessor.put("SCHCRWLR_MCP_SERVER_TRANSPORT", "");
    context = new McpServerContext(envAccessor);
    assertThat(context.mcpTransport(), is(McpServerTransportType.stdio));
  }
}
