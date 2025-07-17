/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.tools.ai.mcpserver.DockerMcpServer.McpServerContext;
import schemacrawler.tools.command.mcpserver.McpServerTransportType;

@DisplayName("DockerMcpServer tests")
public class DockerMcpServerTest {

  private MockEnvironmentVariableAccessor envAccessor;
  private McpServerContext context;

  @BeforeEach
  void setUp() {
    envAccessor = new MockEnvironmentVariableAccessor();
    context = new McpServerContext(envAccessor);
  }

  @Test
  @DisplayName("Should add database credentials when environment variables are set")
  void shouldAddDatabaseCredentials() {
    // Arrange
    final List<String> arguments = new ArrayList<>();
    envAccessor.setenv("SCHCRWLR_DATABASE_USER", "testuser");
    envAccessor.setenv("SCHCRWLR_DATABASE_PASSWORD", "testpass");

    // Act
    context.addDatabaseCredentials(arguments);

    // Assert
    assertThat(arguments, hasSize(4));
    assertThat(arguments, contains("--user:env", "testuser", "--password:env", "testpass"));
  }

  @Test
  @DisplayName("Should add JDBC URL connection when environment variable is set")
  void shouldAddJdbcUrlConnection() {
    // Arrange
    final List<String> arguments = new ArrayList<>();
    envAccessor.setenv("SCHCRWLR_JDBC_URL", "jdbc:test:url");

    // Act
    context.addJdbcUrlConnection(arguments);

    // Assert
    assertThat(arguments, hasSize(2));
    assertThat(arguments, contains("--url", "jdbc:test:url"));
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
      "Should add SchemaCrawler arguments with default values when environment variables are not set")
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
  @DisplayName("Should add server connection arguments when environment variables are set")
  void shouldAddServerConnection() {
    // Arrange
    final List<String> arguments = new ArrayList<>();
    envAccessor.setenv("SCHCRWLR_SERVER", "postgresql");
    envAccessor.setenv("SCHCRWLR_HOST", "localhost");
    envAccessor.setenv("SCHCRWLR_PORT", "5432");
    envAccessor.setenv("SCHCRWLR_DATABASE", "testdb");

    // Mock isValidDatabasePlugin to return true for testing purposes
    final McpServerContext spyContext = spy(context);
    doReturn(true).when(spyContext).isValidDatabasePlugin("postgresql");

    // Act
    spyContext.addServerConnection(arguments);

    // Assert
    assertThat(arguments, hasSize(8));
    assertThat(
        arguments,
        hasItems(
            "--server",
            "postgresql",
            "--host",
            "localhost",
            "--port",
            "5432",
            "--database",
            "testdb"));
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
    assertThat(argList, hasItems("--user:env", "testuser"));
    assertThat(argList, hasItems("--password:env", "testpass"));
  }

  @Test
  @DisplayName("Should build arguments with server connection when JDBC URL is not set")
  void shouldBuildArgumentsWithServerConnection() {
    // Arrange
    envAccessor.setenv("SCHCRWLR_JDBC_URL", null);
    envAccessor.setenv("SCHCRWLR_SERVER", "postgresql");
    envAccessor.setenv("SCHCRWLR_HOST", "localhost");
    envAccessor.setenv("SCHCRWLR_PORT", "5432");
    envAccessor.setenv("SCHCRWLR_DATABASE", "testdb");
    envAccessor.setenv("SCHCRWLR_INFO_LEVEL", InfoLevel.standard.name());
    envAccessor.setenv("SCHCRWLR_LOG_LEVEL", Level.INFO.getName());

    // Mock isValidDatabasePlugin to return true for testing purposes
    final McpServerContext spyContext = spy(context);
    doReturn(true).when(spyContext).isValidDatabasePlugin("postgresql");

    // Act
    final String[] arguments = spyContext.buildArguments();

    // Assert
    assertThat(arguments, notNullValue());
    assertThat(arguments.length, greaterThan(0));
    final List<String> argList = Arrays.asList(arguments);
    assertThat(argList, hasItems("--server", "postgresql"));
    assertThat(argList, hasItems("--host", "localhost"));
  }

  @Test
  @DisplayName("Should not add database credentials when environment variables are not set")
  void shouldNotAddDatabaseCredentialsWhenNotSet() {
    // Arrange
    final List<String> arguments = new ArrayList<>();

    // Act
    context.addDatabaseCredentials(arguments);

    // Assert
    assertThat(arguments, empty());
  }

  @Test
  @DisplayName("Should validate info levels correctly")
  void shouldValidateInfoLevels() {
    // Act & Assert
    assertTrue(context.isValidInfoLevel("standard"));
    assertTrue(context.isValidInfoLevel("detailed"));
    assertTrue(context.isValidInfoLevel("maximum"));
    assertFalse(context.isValidInfoLevel(null));
    assertFalse(context.isValidInfoLevel(""));
    assertFalse(context.isValidInfoLevel("invalid"));
  }

  @Test
  @DisplayName("Should validate log levels correctly")
  void shouldValidateLogLevels() {
    // Act & Assert
    assertTrue(context.isValidLogLevel("INFO"));
    assertTrue(context.isValidLogLevel("WARNING"));
    assertTrue(context.isValidLogLevel("SEVERE"));
    assertTrue(context.isValidLogLevel("FINE"));
    assertFalse(context.isValidLogLevel(null));
    assertFalse(context.isValidLogLevel(""));
    assertFalse(context.isValidLogLevel("invalid"));
  }

  @Test
  @DisplayName("Should validate numeric values correctly")
  void shouldValidateNumericValues() {
    // Act & Assert
    assertTrue(context.isNumeric("123"));
    assertTrue(context.isNumeric("0"));
    assertFalse(context.isNumeric(null));
    assertFalse(context.isNumeric(""));
    assertFalse(context.isNumeric("abc"));
    assertFalse(context.isNumeric("123.45"));
  }
}
