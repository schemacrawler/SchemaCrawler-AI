/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.readconfig.EnvironmentVariableConfig;
import us.fatehi.utility.readconfig.ReadConfig;

public final class LoggingContext {

  private static final Logger LOGGER = Logger.getLogger(LoggingContext.class.getName());

  private static final String LOG_LEVEL = "SCHCRWLR_LOG_LEVEL";

  private final ReadConfig envAccessor;
  private final Level logLevel;

  /** Default constructor that uses System.getenv */
  public LoggingContext() {
    this((EnvironmentVariableConfig) System::getenv);
  }

  /**
   * Constructor with environment variable accessor for testing
   *
   * @param envAccessor The environment variable accessor
   */
  public LoggingContext(final ReadConfig envAccessor) {
    this.envAccessor = requireNonNull(envAccessor, "No environment accessor provided");

    logLevel = readLogLevel();
  }

  /**
   * Returns the configured log level.
   *
   * @return The log level configured from the environment variable
   */
  public Level getLogLevel() {
    return logLevel;
  }

  /**
   * Parses a string and returns a valid log level.
   *
   * @param value The log level string to check
   * @return Level Non-null value
   */
  Level readLogLevel() {

    final Level defaultValue = Level.INFO;

    final String value = envAccessor.getStringValue(LOG_LEVEL, "");
    if (isBlank(value)) {
      return defaultValue;
    }
    try {
      return Level.parse(value.toUpperCase());
    } catch (final Exception e) {
      return defaultValue;
    }
  }
}
