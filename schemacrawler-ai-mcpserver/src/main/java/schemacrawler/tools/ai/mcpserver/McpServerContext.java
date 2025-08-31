/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.nio.file.Path;
import java.util.logging.Level;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptions;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.command.mcpserver.McpServerTransportType;
import schemacrawler.tools.databaseconnector.EnvironmentalDatabaseConnectionSourceBuilder;
import schemacrawler.tools.offline.jdbc.OfflineConnectionUtility;
import us.fatehi.utility.LoggingConfig;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.ioresource.EnvironmentVariableAccessor;

/** Inner class that handles the MCP server setup. */
public final class McpServerContext {

  private final EnvironmentVariableAccessor envAccessor;
  private final McpServerTransportType transport;
  private final SchemaCrawlerOptions schemaCrawlerOptions;

  /** Default constructor that uses System.getenv */
  public McpServerContext() {
    this(System::getenv);
  }

  /**
   * Constructor with environment variable accessor for testing
   *
   * @param envAccessor The environment variable accessor
   */
  protected McpServerContext(final EnvironmentVariableAccessor envAccessor) {
    this.envAccessor = requireNonNull(envAccessor, "No environment accessor provided");

    final Level logLevel = readLogLevel();
    new LoggingConfig(logLevel);

    transport = readTransport();
    schemaCrawlerOptions = buildSchemaCrawlerOptions();
  }

  public SchemaCrawlerOptions getSchemaCrawlerOptions() {
    return schemaCrawlerOptions;
  }

  public McpServerTransportType mcpTransport() {
    return transport;
  }

  protected DatabaseConnectionSource buildCatalogDatabaseConnectionSource() {

    final String offlineDatabasePathString =
        trimToEmpty(envAccessor.getenv("SCHCRWLR_OFFLINE_DATABASE"));
    if (isBlank(offlineDatabasePathString)) {
      return buildOperationsDatabaseConnectionSource();
    }

    final Path offlineDatabasePath = Path.of(offlineDatabasePathString);
    final DatabaseConnectionSource dbConnectionSource =
        OfflineConnectionUtility.newOfflineDatabaseConnectionSource(offlineDatabasePath);
    return dbConnectionSource;
  }

  /**
   * Builds the complete argument list from environment variables.
   *
   * @return List of command line arguments
   */
  protected DatabaseConnectionSource buildOperationsDatabaseConnectionSource() {

    final DatabaseConnectionSource databaseConnectionSource =
        EnvironmentalDatabaseConnectionSourceBuilder.builder(envAccessor).build();
    return databaseConnectionSource;
  }

  /**
   * Adds SchemaCrawler specific arguments to the arguments list.
   *
   * @param arguments The list of arguments to add to
   */
  protected SchemaCrawlerOptions buildSchemaCrawlerOptions() {
    final InfoLevel infoLevel = readInfoLevel();

    final LoadOptions loadOptions = LoadOptionsBuilder.builder().withInfoLevel(infoLevel).build();
    final LimitOptions limitOptions =
        LimitOptionsBuilder.builder()
            .includeAllRoutines()
            .includeAllSequences()
            .includeAllSynonyms()
            .includeAllTables()
            .build();

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLoadOptions(loadOptions)
            .withLimitOptions(limitOptions);
    return schemaCrawlerOptions;
  }

  /**
   * Parses a string and returns a valid SchemaCrawler info level.
   *
   * @param value The info level string to check
   * @return InfoLevel Non-null value
   */
  protected InfoLevel readInfoLevel() {

    final InfoLevel defaultValue = InfoLevel.standard;
    try {
      final String value = envAccessor.getenv("SCHCRWLR_INFO_LEVEL");
      InfoLevel infoLevel = InfoLevel.valueOfFromString(value);
      if (infoLevel == InfoLevel.unknown) {
        infoLevel = defaultValue;
      }
      return infoLevel;
    } catch (final Exception e) {
      return defaultValue;
    }
  }

  /**
   * Parses a string and returns a valid log level.
   *
   * @param value The log level string to check
   * @return Level Non-null value
   */
  protected Level readLogLevel() {

    final Level defaultValue = Level.INFO;

    final String value = envAccessor.getenv("SCHCRWLR_LOG_LEVEL");
    if (isBlank(value)) {
      return defaultValue;
    }
    try {
      return Level.parse(value);
    } catch (final Exception e) {
      return defaultValue;
    }
  }

  /**
   * Parses a string and returns a valid transport.
   *
   * @param value The transport string to check
   * @return McpServerTransportType Non-null value
   */
  protected McpServerTransportType readTransport() {
    requireNonNull(envAccessor, "No environmental accessor provided");

    final McpServerTransportType defaultValue = McpServerTransportType.stdio;

    final String value = envAccessor.getenv("SCHCRWLR_MCP_SERVER_TRANSPORT");
    if (isBlank(value)) {
      return defaultValue;
    }
    try {
      McpServerTransportType transport = McpServerTransportType.valueOf(value);
      if (transport == McpServerTransportType.unknown) {
        transport = defaultValue;
      }
      return transport;
    } catch (final Exception e) {
      return defaultValue;
    }
  }
}
