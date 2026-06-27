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

@Component
public class LoggingInitializer extends AbstractExecutionState
    implements ApplicationContextInitializer<GenericApplicationContext> {

  private static final Logger LOGGER = Logger.getLogger(LoggingInitializer.class.getName());

  @Override
  public void initialize(@NonNull final GenericApplicationContext context) {
    initialize();
  }

  @EventListener
  public void onContextRefreshed(final ContextRefreshedEvent event) {
    initialize();
  }

  public static void initialize() {
    final Level logLevel = new LoggingContext().getLogLevel();

    // Install JUL-to-SLF4J bridge to route JUL logs through Logback (Spring Boot
    // logging)
    try {
      SLF4JBridgeHandler.removeHandlersForRootLogger();
      SLF4JBridgeHandler.install();

      LogManager.getLogManager().getLogger("");
      final Logger rootLogger = Logger.getLogger("");
      rootLogger.setLevel(logLevel);

      LOGGER.log(
          Level.FINE, () -> "Installed JUL-to-SLF4J bridge for Spring Boot logging integration");

      final LoggingSystem loggingSystem = LoggingSystem.get(McpServer.class.getClassLoader());

      // Set SchemaCrawler log levels
      loggingSystem.setLogLevel("schemacrawler", LogLevel.DEBUG);
      loggingSystem.setLogLevel("us.fatehi", LogLevel.DEBUG);
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not restore JUL log levels after Spring init", e);
    }
  }
}
