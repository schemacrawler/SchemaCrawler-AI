/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.ai.mcpserver.test.MockEnvironmentVariableAccessor;

@DisplayName("MCP Server configuration tests")
public class McpServerContextTest {

  private MockEnvironmentVariableAccessor envAccessor;
  private McpServerContext context;

  @BeforeEach
  void setUp() {
    envAccessor = new MockEnvironmentVariableAccessor();
  }

  @Test
  @DisplayName("Should build SchemaCrawler options when context is created")
  void shouldBuildSchemaCrawlerOptions() {
    // Arrange
    envAccessor.setenv("SCHCRWLR_INFO_LEVEL", "detailed");
    context = new McpServerContext(envAccessor);

    // Act
    final SchemaCrawlerOptions options = context.getSchemaCrawlerOptions();

    // Assert
    assertThat(options, notNullValue());
    assertThat(options.loadOptions(), notNullValue());
    assertThat(options.limitOptions(), notNullValue());
    assertThat(options.loadOptions().schemaInfoLevel().getTag(), is("detailed"));
  }

  @Test
  @DisplayName("Should handle blanks, spaces, and duplicates in exclude tools")
  void shouldHandleBlanksSpacesAndDuplicatesInExcludeTools() {
    // Arrange
    envAccessor.setenv("SCHCRWLR_EXCLUDE_TOOLS", "  toolA , , toolB,toolA ,  ,ToolA  ");
    context = new McpServerContext(envAccessor);

    // Act
    final java.util.Collection<String> excluded = context.excludeTools();

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
    envAccessor.setenv("SCHCRWLR_EXCLUDE_TOOLS", "tool1,tool2,tool3");
    context = new McpServerContext(envAccessor);

    // Act
    final java.util.Collection<String> excluded = context.excludeTools();

    // Assert
    assertThat(excluded.contains("tool1"), is(true));
    assertThat(excluded.contains("tool2"), is(true));
    assertThat(excluded.contains("tool3"), is(true));
    assertThat(excluded.size(), is(3));
  }

  @Test
  @DisplayName("Should read info level with custom values when environment variables are set")
  void shouldReadInfoLevelWithCustomValues() {
    // Arrange
    envAccessor.setenv("SCHCRWLR_INFO_LEVEL", "detailed");
    context = new McpServerContext(envAccessor);

    // Act
    final InfoLevel infoLevel = context.readInfoLevel();

    // Assert
    assertThat(infoLevel, is(InfoLevel.detailed));
  }

  @Test
  @DisplayName("Should read info level with default values when environment variables are not set")
  void shouldReadInfoLevelWithDefaults() {
    // Arrange
    envAccessor.setenv("SCHCRWLR_INFO_LEVEL", null);
    context = new McpServerContext(envAccessor);

    // Act
    final InfoLevel infoLevel = context.readInfoLevel();

    // Assert
    assertThat(infoLevel, is(InfoLevel.standard));
  }

  @Test
  @DisplayName("Should return empty set when SCHCRWLR_EXCLUDE_TOOLS is unset or empty")
  void shouldReturnEmptySetWhenExcludeToolsUnsetOrEmpty() {
    // Unset (null)
    envAccessor.setenv("SCHCRWLR_EXCLUDE_TOOLS", null);
    context = new McpServerContext(envAccessor);
    java.util.Collection<String> excluded = context.excludeTools();
    assertThat(excluded.size(), is(0));

    // Empty string
    envAccessor.setenv("SCHCRWLR_EXCLUDE_TOOLS", "");
    context = new McpServerContext(envAccessor);
    excluded = context.excludeTools();
    assertThat(excluded.size(), is(0));

    // Only commas and spaces
    envAccessor.setenv("SCHCRWLR_EXCLUDE_TOOLS", ", , ,  ,");
    context = new McpServerContext(envAccessor);
    excluded = context.excludeTools();
    assertThat(excluded.size(), is(0));
  }

  @Test
  @DisplayName("Should validate info levels correctly")
  void shouldValidateInfoLevels() {
    // Test standard level
    envAccessor.setenv("SCHCRWLR_INFO_LEVEL", "standard");
    context = new McpServerContext(envAccessor);
    assertThat(context.readInfoLevel(), is(InfoLevel.standard));

    // Test detailed level
    envAccessor.setenv("SCHCRWLR_INFO_LEVEL", "detailed");
    context = new McpServerContext(envAccessor);
    assertThat(context.readInfoLevel(), is(InfoLevel.detailed));

    // Test maximum level
    envAccessor.setenv("SCHCRWLR_INFO_LEVEL", "maximum");
    context = new McpServerContext(envAccessor);
    assertThat(context.readInfoLevel(), is(InfoLevel.maximum));

    // Test null defaults to standard
    envAccessor.setenv("SCHCRWLR_INFO_LEVEL", null);
    context = new McpServerContext(envAccessor);
    assertThat(context.readInfoLevel(), is(InfoLevel.standard));

    // Test empty string defaults to standard
    envAccessor.setenv("SCHCRWLR_INFO_LEVEL", "");
    context = new McpServerContext(envAccessor);
    assertThat(context.readInfoLevel(), is(InfoLevel.standard));

    // Test invalid value defaults to standard
    envAccessor.setenv("SCHCRWLR_INFO_LEVEL", "invalid");
    context = new McpServerContext(envAccessor);
    assertThat(context.readInfoLevel(), is(InfoLevel.standard));
  }

  @Test
  @DisplayName("Should validate log levels correctly")
  void shouldValidateLogLevels() {
    // Test INFO level
    envAccessor.setenv("SCHCRWLR_LOG_LEVEL", "INFO");
    context = new McpServerContext(envAccessor);
    assertThat(context.readLogLevel().getName(), is("INFO"));

    // Test WARNING level
    envAccessor.setenv("SCHCRWLR_LOG_LEVEL", "WARNING");
    context = new McpServerContext(envAccessor);
    assertThat(context.readLogLevel().getName(), is("WARNING"));

    // Test SEVERE level
    envAccessor.setenv("SCHCRWLR_LOG_LEVEL", "SEVERE");
    context = new McpServerContext(envAccessor);
    assertThat(context.readLogLevel().getName(), is("SEVERE"));

    // Test FINE level
    envAccessor.setenv("SCHCRWLR_LOG_LEVEL", "FINE");
    context = new McpServerContext(envAccessor);
    assertThat(context.readLogLevel().getName(), is("FINE"));

    // Test null defaults to INFO
    envAccessor.setenv("SCHCRWLR_LOG_LEVEL", null);
    context = new McpServerContext(envAccessor);
    assertThat(context.readLogLevel().getName(), is("INFO"));

    // Test empty string defaults to INFO
    envAccessor.setenv("SCHCRWLR_LOG_LEVEL", "");
    context = new McpServerContext(envAccessor);
    assertThat(context.readLogLevel().getName(), is("INFO"));

    // Test invalid value defaults to INFO
    envAccessor.setenv("SCHCRWLR_LOG_LEVEL", "invalid");
    context = new McpServerContext(envAccessor);
    assertThat(context.readLogLevel().getName(), is("INFO"));
  }

  @Test
  @DisplayName("Should validate transport correctly")
  void shouldValidateTransport() {
    // Test stdio transport
    envAccessor.setenv("SCHCRWLR_MCP_SERVER_TRANSPORT", "stdio");
    context = new McpServerContext(envAccessor);
    assertThat(context.getMcpTransport(), is(McpServerTransportType.stdio));

    // Test http transport
    envAccessor.setenv("SCHCRWLR_MCP_SERVER_TRANSPORT", "http");
    context = new McpServerContext(envAccessor);
    assertThat(context.getMcpTransport(), is(McpServerTransportType.http));

    // Test unknown value defaults to stdio
    envAccessor.setenv("SCHCRWLR_MCP_SERVER_TRANSPORT", "unknown");
    context = new McpServerContext(envAccessor);
    assertThat(context.getMcpTransport(), is(McpServerTransportType.stdio));

    // Test null defaults to stdio
    envAccessor.setenv("SCHCRWLR_MCP_SERVER_TRANSPORT", null);
    context = new McpServerContext(envAccessor);
    assertThat(context.getMcpTransport(), is(McpServerTransportType.stdio));

    // Test empty string defaults to stdio
    envAccessor.setenv("SCHCRWLR_MCP_SERVER_TRANSPORT", "");
    context = new McpServerContext(envAccessor);
    assertThat(context.getMcpTransport(), is(McpServerTransportType.stdio));
  }
}
