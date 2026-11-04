/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.utility;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.mockito.MockedStatic;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;
import us.fatehi.utility.property.JvmSystemInfo;
import us.fatehi.utility.property.OperatingSystemInfo;

@ResourceLock("startup-logger")
class LoggingUtilityTest {

  @Test
  void logsRevisionAlongsidePlatformInformation() {
    final List<String> messages;
    try (final MockedStatic<LoggingUtility> logging =
        mockStatic(LoggingUtility.class, CALLS_REAL_METHODS)) {
      logging
          .when(LoggingUtility::readGitBuildInfo)
          .thenReturn(new GitBuildInfo("abc123", "2026-11-02T00:00:00Z"));
      messages = capture(Level.INFO, () -> LoggingUtility.logStartup(McpServerTransportType.stdio));
    }

    assertThat(messages.size(), is(1));
    assertThat(messages.getFirst(), containsString("Git information:"));
    assertThat(messages.getFirst(), containsString("Commit SHA:abc123"));
    assertThat(messages.getFirst(), containsString("Commit time:2026-11-02T00:00:00Z"));
    assertThat(messages.getFirst(), containsString("Spring Boot"));
    assertThat(messages.getFirst(), containsString(JvmSystemInfo.jvmSystemInfo().toString()));
    assertThat(
        messages.getFirst(), containsString(OperatingSystemInfo.operatingSystemInfo().toString()));
  }

  @Test
  void unavailableMetadataDoesNotSuppressPlatformDiagnostics() {
    final List<String> messages;
    try (final MockedStatic<LoggingUtility> logging =
        mockStatic(LoggingUtility.class, CALLS_REAL_METHODS)) {
      logging.when(LoggingUtility::readGitBuildInfo).thenReturn(new GitBuildInfo(null, null));
      messages = capture(Level.INFO, () -> LoggingUtility.logStartup(McpServerTransportType.stdio));
    }

    assertThat(messages.size(), is(1));
    assertThat(messages.getFirst(), not(containsString("Git information:")));
    assertThat(messages.getFirst(), containsString("Spring Boot"));
    assertThat(messages.getFirst(), containsString(JvmSystemInfo.jvmSystemInfo().toString()));
  }

  @Test
  void respectsInfoLoggingGate() {
    assertThat(
        capture(Level.WARNING, () -> LoggingUtility.logStartup(McpServerTransportType.stdio))
            .isEmpty(),
        is(true));
  }

  private static List<String> capture(final Level level, final Runnable action) {
    final Logger logger = Logger.getLogger(LoggingUtility.class.getCanonicalName());
    final Level previousLevel = logger.getLevel();
    final boolean previousUseParentHandlers = logger.getUseParentHandlers();
    final List<String> messages = new ArrayList<>();
    final Handler handler =
        new Handler() {
          @Override
          public void publish(final LogRecord record) {
            messages.add(record.getMessage());
          }

          @Override
          public void flush() {}

          @Override
          public void close() {}
        };
    try {
      logger.setLevel(level);
      logger.setUseParentHandlers(false);
      logger.addHandler(handler);
      action.run();
      return messages;
    } finally {
      logger.removeHandler(handler);
      logger.setLevel(previousLevel);
      logger.setUseParentHandlers(previousUseParentHandlers);
    }
  }
}
