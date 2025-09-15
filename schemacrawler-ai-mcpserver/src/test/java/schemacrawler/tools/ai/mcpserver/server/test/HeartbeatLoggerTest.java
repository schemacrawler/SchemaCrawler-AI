/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import schemacrawler.tools.ai.mcpserver.server.ConfigurationManager;
import schemacrawler.tools.ai.mcpserver.server.HeartbeatLogger;
import schemacrawler.tools.command.mcpserver.McpServerTransportType;

@ExtendWith(MockitoExtension.class)
class HeartbeatLoggerTest {

  /** Custom log handler for testing purposes */
  private static class TestLogHandler extends Handler {
    private final List<LogRecord> logRecords = new ArrayList<>();

    public void clearLogs() {
      logRecords.clear();
    }

    @Override
    public void close() throws SecurityException {
      logRecords.clear();
    }

    @Override
    public void flush() {
      // No-op for testing
    }

    public List<LogRecord> getLogRecords() {
      return new ArrayList<>(logRecords);
    }

    @Override
    public void publish(final LogRecord record) {
      logRecords.add(record);
    }
  }

  @Mock private ConfigurationManager configurationManager;
  private HeartbeatLogger heartbeatLogger;
  private TestLogHandler logHandler;

  private Logger logger;

  @BeforeEach
  void setUp() {
    heartbeatLogger = new HeartbeatLogger();

    // Set up test configuration
    ReflectionTestUtils.setField(heartbeatLogger, "heartbeat", true);
    ReflectionTestUtils.setField(heartbeatLogger, "serverName", "TestServer");
    ReflectionTestUtils.setField(heartbeatLogger, "serverVersion", "1.0.0");

    // Set up test log handler
    logHandler = new TestLogHandler();
    logger = Logger.getLogger(HeartbeatLogger.class.getCanonicalName());
    logger.addHandler(logHandler);
    logger.setLevel(Level.ALL);
  }

  @Test
  void testHeartbeatMessage_ShouldContainAllExpectedFields() {
    // Given
    try (final MockedStatic<ConfigurationManager> mockedConfigManager =
        mockStatic(ConfigurationManager.class)) {
      mockedConfigManager.when(ConfigurationManager::getInstance).thenReturn(configurationManager);
      when(configurationManager.isInErrorState()).thenReturn(false);
      when(configurationManager.getMcpTransport()).thenReturn(McpServerTransportType.sse);

      heartbeatLogger.init();
      heartbeatLogger.logHeartbeat();

      // Then
      final LogRecord logRecord = logHandler.getLogRecords().get(1); // Second log (first is from
      // init)
      final String logMessage = logRecord.getMessage().toString();

      assertThat(
          "Should contain server info",
          logMessage,
          containsString("\"_server\" : \"TestServer 1.0.0\""));
      assertThat("Should contain error state", logMessage, containsString("\"in-error-state\""));
      assertThat("Should contain uptime", logMessage, containsString("\"server-uptime\""));
      assertThat(
          "Should contain transport type", logMessage, containsString("\"transport\" : \"sse\""));
      assertThat(
          "Should be JSON formatted", logMessage, allOf(containsString("{"), containsString("}")));
    }
  }

  @Test
  void testHeartbeatMessage_WithDifferentTransportTypes_ShouldReflectCorrectTransport() {
    // Test each transport type
    final McpServerTransportType[] transports = {
      McpServerTransportType.stdio, McpServerTransportType.sse
    };

    for (final McpServerTransportType transport : transports) {
      try (final MockedStatic<ConfigurationManager> mockedConfigManager =
          mockStatic(ConfigurationManager.class)) {
        mockedConfigManager
            .when(ConfigurationManager::getInstance)
            .thenReturn(configurationManager);
        when(configurationManager.isInErrorState()).thenReturn(false);
        when(configurationManager.getMcpTransport()).thenReturn(transport);

        logHandler.clearLogs();
        heartbeatLogger.init();

        final LogRecord logRecord = logHandler.getLogRecords().get(0);
        final String logMessage = logRecord.getMessage().toString();

        assertThat(
            String.format("Should contain transport %s", transport.name()),
            logMessage,
            containsString(String.format("\"transport\" : \"%s\"", transport.name())));
      }
    }
  }

  @Test
  void testInit_ShouldInitializeFieldsAndLogHeartbeat() {
    // Given
    try (MockedStatic<ConfigurationManager> mockedConfigManager =
        mockStatic(ConfigurationManager.class)) {
      mockedConfigManager.when(ConfigurationManager::getInstance).thenReturn(configurationManager);
      when(configurationManager.isInErrorState()).thenReturn(false);
      when(configurationManager.getMcpTransport()).thenReturn(McpServerTransportType.stdio);

      // When
      heartbeatLogger.init();

      // Then
      assertThat(
          "ConfigurationManager getInstance should be called",
          configurationManager,
          is(notNullValue()));
      verify(configurationManager).isInErrorState();
      verify(configurationManager).getMcpTransport();

      // Verify logging occurred
      assertThat(
          "Should have logged heartbeat message",
          logHandler.getLogRecords(),
          hasSize(greaterThan(0)));

      final LogRecord logRecord = logHandler.getLogRecords().get(0);
      assertThat("Log level should be INFO", logRecord.getLevel(), is(Level.INFO));
      assertThat(
          "Log message should contain server info",
          logRecord.getMessage().toString(),
          containsString("TestServer 1.0.0"));
    }
  }

  @Test
  void testInit_WithErrorState_ShouldReflectErrorState() {
    // Given
    try (MockedStatic<ConfigurationManager> mockedConfigManager =
        mockStatic(ConfigurationManager.class)) {
      mockedConfigManager.when(ConfigurationManager::getInstance).thenReturn(configurationManager);
      when(configurationManager.isInErrorState()).thenReturn(true);
      when(configurationManager.getMcpTransport()).thenReturn(McpServerTransportType.sse);

      // When
      heartbeatLogger.init();

      // Then
      final LogRecord logRecord = logHandler.getLogRecords().get(0);
      assertThat(
          "Log message should indicate error state",
          logRecord.getMessage().toString(),
          containsString("\"in-error-state\" : \"true\""));
      assertThat(
          "Log message should contain SSE transport",
          logRecord.getMessage().toString(),
          containsString("\"transport\" : \"sse\""));
    }
  }

  @Test
  void testLogHeartbeat_WhenHeartbeatDisabled_ShouldNotLog() {
    // Given
    ReflectionTestUtils.setField(heartbeatLogger, "heartbeat", false);
    try (MockedStatic<ConfigurationManager> mockedConfigManager =
        mockStatic(ConfigurationManager.class)) {
      mockedConfigManager.when(ConfigurationManager::getInstance).thenReturn(configurationManager);
      when(configurationManager.isInErrorState()).thenReturn(false);
      when(configurationManager.getMcpTransport()).thenReturn(McpServerTransportType.stdio);

      heartbeatLogger.init();
      logHandler.clearLogs();

      // When
      heartbeatLogger.logHeartbeat();

      // Then
      assertThat("Should not have logged any message", logHandler.getLogRecords(), hasSize(0));
    }
  }

  @Test
  void testLogHeartbeat_WhenHeartbeatEnabled_ShouldLogMessage() {
    // Given
    ReflectionTestUtils.setField(heartbeatLogger, "heartbeat", true);
    try (MockedStatic<ConfigurationManager> mockedConfigManager =
        mockStatic(ConfigurationManager.class)) {
      mockedConfigManager.when(ConfigurationManager::getInstance).thenReturn(configurationManager);
      when(configurationManager.isInErrorState()).thenReturn(false);
      when(configurationManager.getMcpTransport()).thenReturn(McpServerTransportType.stdio);

      heartbeatLogger.init();
      logHandler.clearLogs();

      // When
      heartbeatLogger.logHeartbeat();

      // Then
      assertThat("Should have logged heartbeat message", logHandler.getLogRecords(), hasSize(1));

      final LogRecord logRecord = logHandler.getLogRecords().get(0);
      assertThat("Log level should be INFO", logRecord.getLevel(), is(Level.INFO));
      assertThat(
          "Log message should contain server name",
          logRecord.getMessage().toString(),
          containsString("TestServer"));
      assertThat(
          "Log message should contain version",
          logRecord.getMessage().toString(),
          containsString("1.0.0"));
    }
  }
}
