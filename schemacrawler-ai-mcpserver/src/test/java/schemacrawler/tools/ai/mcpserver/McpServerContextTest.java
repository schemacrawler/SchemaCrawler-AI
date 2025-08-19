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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.tools.ai.mcpserver.test.MockEnvironmentVariableAccessor;
import schemacrawler.tools.command.mcpserver.McpServerTransportType;

@DisplayName("Docker MCP Server configuration tests")
public class McpServerContextTest {

  private MockEnvironmentVariableAccessor envAccessor;
  private McpServerContext context;

  @BeforeEach
  void setUp() {
    envAccessor = new MockEnvironmentVariableAccessor();
    context = new McpServerContext(envAccessor);
  }

  @Test
  @DisplayName(
      "Should add SchemaCrawler arguments with custom values when environment variables are set")
  void shouldAddSchemaCrawlerArgumentsWithCustomValues() {
    // Arrange
    final List<String> arguments = new ArrayList<>();

    envAccessor.setenv("SCHCRWLR_INFO_LEVEL", "detailed");
    envAccessor.setenv("SCHCRWLR_LOG_LEVEL", "FINE");

    // Act
    context.addSchemaCrawlerArguments(arguments);

    // Assert
    assertThat(arguments, hasSize(10));
    assertThat(arguments, hasItems("--info-level", "detailed"));
    assertThat(arguments, hasItems("--log-level", "FINE"));
  }

  @Test
  @DisplayName(
      "Should add SchemaCrawler arguments with default values when environment variables are not"
          + " set")
  void shouldAddSchemaCrawlerArgumentsWithDefaults() {
    // Arrange
    final List<String> arguments = new ArrayList<>();
    envAccessor.setenv("SCHCRWLR_INFO_LEVEL", null);
    envAccessor.setenv("SCHCRWLR_LOG_LEVEL", null);

    // Act
    context.addSchemaCrawlerArguments(arguments);

    // Assert
    assertThat(arguments, hasSize(10));
    assertThat(
        arguments,
        hasItems(
            "--info-level",
            InfoLevel.standard.name(),
            "--log-level",
            Level.INFO.getName(),
            "--routines",
            ".*",
            "--command",
            "mcpserver",
            "--transport",
            McpServerTransportType.stdio.name()));
  }

  @Test
  @DisplayName("Should build arguments with JDBC URL connection when JDBC URL is set")
  void shouldBuildArgumentsWithJdbcUrl() {
    // Arrange
    envAccessor.setenv("SCHCRWLR_JDBC_URL", "jdbc:test:url");
    envAccessor.setenv("SCHCRWLR_DATABASE_USER", "testuser");
    envAccessor.setenv("SCHCRWLR_DATABASE_PASSWORD", "testpass");
    envAccessor.setenv("SCHCRWLR_INFO_LEVEL", null);
    envAccessor.setenv("SCHCRWLR_LOG_LEVEL", null);

    // Act
    final String[] arguments = context.buildArguments();

    // Assert
    assertThat(arguments, notNullValue());
    assertThat(arguments.length, greaterThan(0));
    final List<String> argList = Arrays.asList(arguments);
    assertThat(argList, hasItems("--url", "jdbc:test:url"));
    assertThat(argList, hasItems("--user", "testuser"));
    assertThat(argList, hasItems("--password", "testpass"));
  }

  @Test
  @DisplayName("Should validate info levels correctly")
  void shouldValidateInfoLevels() {
    assertThat(context.validInfoLevel("standard"), is(InfoLevel.standard));
    assertThat(context.validInfoLevel("detailed"), is(InfoLevel.detailed));
    assertThat(context.validInfoLevel("maximum"), is(InfoLevel.maximum));
    assertThat(context.validInfoLevel(null), is(InfoLevel.standard));
    assertThat(context.validInfoLevel(""), is(InfoLevel.standard));
    assertThat(context.validInfoLevel("invalid"), is(InfoLevel.standard));
  }

  @Test
  @DisplayName("Should validate log levels correctly")
  void shouldValidateLogLevels() {
    // Act & Assert
    assertThat(context.validLogLevel("INFO").getName(), is("INFO"));
    assertThat(context.validLogLevel("WARNING").getName(), is("WARNING"));
    assertThat(context.validLogLevel("SEVERE").getName(), is("SEVERE"));
    assertThat(context.validLogLevel("FINE").getName(), is("FINE"));
    assertThat(context.validLogLevel(null).getName(), is("INFO"));
    assertThat(context.validLogLevel("").getName(), is("INFO"));
    assertThat(context.validLogLevel("invalid").getName(), is("INFO"));
  }
}
