/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver;

import java.util.logging.Level;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import schemacrawler.tools.ai.mcpserver.McpServerMain.McpServer;
import schemacrawler.tools.state.AbstractExecutionState;
import us.fatehi.utility.LoggingConfig;

/**
 * Initializes and configures the JUL and SLF4J logging systems for the MCP Server.
 *
 * <p>This component serves a dual role: 1. ApplicationContextInitializer: Initializes logging
 * during Spring context creation 2. EventListener: Restores logging configuration after Spring Boot
 * initializes
 *
 * <p>The initialization process reads SCHCRWLR_LOG_LEVEL from environment variable (via
 * LoggingContext), configures SchemaCrawler loggers and maps JUL log level to Spring LogLevel.
 */
@Component
public class LoggingInitializer extends AbstractExecutionState
    implements ApplicationContextInitializer<GenericApplicationContext> {

  /**
   * Main initialization logic for logging configuration. This method coordinates the setup of both
   * JUL and SLF4J logging systems.
   */
  public static void initialize() {
    try {
      // Step 1: Read the configured log level from environment variable
      final Level logLevel = new LoggingContext().getLogLevel();

      // Step 2: Configure JUL root logger to the specified level
      new LoggingConfig(logLevel);

      // Step 3: Configure loggers in Spring Boot (SLF4J/Logback) to log to configured level
      final LoggingSystem loggingSystem = LoggingSystem.get(McpServer.class.getClassLoader());
      final LogLevel slF4jLogLevel = mapJulLevelToSlf4j(logLevel);
      loggingSystem.setLogLevel(null, slF4jLogLevel);

    } catch (final Exception e) {
      // Do log error, since we are not able to set up the logging system
      // Print stack trace on stderr
      e.printStackTrace(System.err);
    }
  }

  /**
   * Maps a JUL log level to an equivalent Spring Boot SLF4J log level.
   *
   * <p>JUL and SLF4J use different level hierarchies: - JUL: OFF, SEVERE, WARNING, INFO, CONFIG,
   * FINE, FINER, FINEST - SLF4J: OFF, ERROR, WARN, INFO, DEBUG, TRACE
   *
   * @param julLevel The JUL log level to map
   * @return The corresponding SLF4J LogLevel
   */
  private static LogLevel mapJulLevelToSlf4j(final Level julLevel) {
    if (julLevel == null) {
      return LogLevel.INFO;
    }

    switch (julLevel.getName()) {
      case "OFF":
        return LogLevel.OFF;
      case "SEVERE":
        return LogLevel.ERROR;
      case "WARNING":
        return LogLevel.WARN;
      case "INFO":
      case "CONFIG":
        return LogLevel.INFO;
      case "ALL":
      case "FINE":
      case "FINER":
      case "FINEST":
        return LogLevel.DEBUG;
      default:
        return LogLevel.INFO;
    }
  }

  /**
   * Initialize during Spring context creation (ApplicationContextInitializer phase).
   *
   * @param context The Spring application context
   */
  @Override
  public void initialize(@NonNull final GenericApplicationContext context) {
    initialize();
  }

  /**
   * Restore logging configuration after Spring context is fully refreshed.
   *
   * @param event The context refreshed event
   */
  @EventListener
  public void onContextRefreshed(final ContextRefreshedEvent event) {
    initialize();
  }
}
