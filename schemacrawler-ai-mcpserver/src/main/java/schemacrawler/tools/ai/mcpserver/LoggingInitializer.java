/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.jspecify.annotations.NonNull;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;
import schemacrawler.tools.ai.mcpserver.McpServerMain.McpServer;
import schemacrawler.tools.state.AbstractExecutionState;

/**
 * Initializes and configures the JUL and SLF4J logging systems for the MCP Server.
 *
 * <p>This component serves a dual role: 1. ApplicationContextInitializer: Initializes logging
 * during Spring context creation 2. EventListener: Restores logging configuration after Spring Boot
 * initializes
 *
 * <p>The initialization process: 1. Reads SCHCRWLR_LOG_LEVEL from environment variable (via
 * LoggingContext) 2. Installs JUL-to-SLF4J bridge to route all JUL logs through Logback 3.
 * Configures JUL root logger to the specified level 4. Maps JUL log level to Spring LogLevel and
 * configures SchemaCrawler loggers
 *
 * <p>Why both JUL and SLF4J configuration? - SchemaCrawler uses JUL (java.util.logging) - Spring
 * Boot uses SLF4J/Logback - We need to bridge JUL→SLF4J and ensure both systems respect the
 * configured level
 */
@Component
public class LoggingInitializer extends AbstractExecutionState
    implements ApplicationContextInitializer<GenericApplicationContext> {

  private static final Logger LOGGER = Logger.getLogger(LoggingInitializer.class.getName());

  /**
   * Main initialization logic for logging configuration. This method coordinates the setup of both
   * JUL and SLF4J logging systems.
   */
  public static void initialize() {
    try {
      // Step 1: Read the configured log level from environment variable
      final Level logLevel = new LoggingContext().getLogLevel();

      // Step 2: Install JUL-to-SLF4J bridge to route JUL logs through Logback (Spring Boot
      // logging)
      SLF4JBridgeHandler.removeHandlersForRootLogger();
      SLF4JBridgeHandler.install();

      // Step 3: Configure JUL root logger to the specified level
      LogManager.getLogManager().getLogger("");
      final Logger rootLogger = Logger.getLogger("");
      rootLogger.setLevel(logLevel);

      LOGGER.log(
          Level.FINE,
          () ->
              "Installed JUL-to-SLF4J bridge and configured root logger to " + logLevel.getName());

      // Step 4: Configure SchemaCrawler loggers in Spring Boot (SLF4J/Logback)
      final LoggingSystem loggingSystem = LoggingSystem.get(McpServer.class.getClassLoader());
      final LogLevel slF4jLogLevel = mapJulLevelToSlf4j(logLevel);

      loggingSystem.setLogLevel("schemacrawler", slF4jLogLevel);
      loggingSystem.setLogLevel("us.fatehi", slF4jLogLevel);

      LOGGER.log(
          Level.FINE,
          () ->
              "Configured SchemaCrawler loggers to SLF4J level "
                  + slF4jLogLevel
                  + " (mapped from JUL level "
                  + logLevel.getName()
                  + ")");

    } catch (final Exception e) {
      LOGGER.log(
          Level.WARNING,
          "Could not initialize logging (JUL-to-SLF4J bridge, root logger, or SchemaCrawler logger"
              + " configuration failed)",
          e);
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
