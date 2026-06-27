/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import us.fatehi.utility.LoggingConfig;

/**
 * Listens for Spring context initialization and restores JUL log levels for SchemaCrawler loggers.
 * This ensures that the log level configured via {@code SCHCRWLR_LOG_LEVEL} environment variable is
 * honored throughout the server lifecycle, even after Spring Boot reconfigures logging.
 */
@Component
public final class LoggingRestorationListener {

  private static final Logger LOGGER = Logger.getLogger(LoggingRestorationListener.class.getName());

  private final Level logLevel;

  /**
   * Constructor that injects the log level bean.
   *
   * @param logLevel The configured log level
   */
  public LoggingRestorationListener(@Qualifier("logLevel") final Level logLevel) {
    this.logLevel = logLevel;
  }

  /**
   * Restores JUL log levels after Spring context initialization is complete.
   *
   * @param event The context refreshed event
   */
  @EventListener
  public void onContextRefreshed(final ContextRefreshedEvent event) {
    if (logLevel == null) {
      LOGGER.log(Level.WARNING, "Log level is null; skipping log level restoration");
      return;
    }

    try {
      new LoggingConfig(logLevel);

      LOGGER.log(
          Level.FINE,
          () -> "Restored log level to <%s> after Spring Boot initialization".formatted(logLevel));
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not restore JUL log levels after Spring init", e);
    }
  }
}
