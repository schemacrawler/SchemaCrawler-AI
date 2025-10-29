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
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptions;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.databaseconnector.EnvironmentalDatabaseConnectionSourceBuilder;
import schemacrawler.tools.offline.jdbc.OfflineConnectionUtility;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.LoggingConfig;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.ioresource.EnvironmentVariableAccessor;

/** Inner class that handles the MCP server setup. */
final class McpServerContext {

  private static final String EXCLUDE_TOOLS = "SCHCRWLR_EXCLUDE_TOOLS";
  private static final String INFO_LEVEL = "SCHCRWLR_INFO_LEVEL";
  private static final String LOG_LEVEL = "SCHCRWLR_LOG_LEVEL";
  private static final String MCP_SERVER_TRANSPORT = "SCHCRWLR_MCP_SERVER_TRANSPORT";
  private static final String OFFLINE_DATABASE = "SCHCRWLR_OFFLINE_DATABASE";

  private final EnvironmentVariableAccessor envAccessor;
  private final McpServerTransportType transport;
  private final SchemaCrawlerOptions schemaCrawlerOptions;

  /** Default constructor that uses System.getenv */
  protected McpServerContext() {
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

  protected DatabaseConnectionSource buildCatalogDatabaseConnectionSource() {
    final String offlineDatabasePathString = trimToEmpty(envAccessor.getenv(OFFLINE_DATABASE));
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

  protected Collection<String> excludeTools() {
    final String input = trimToEmpty(envAccessor.getenv(EXCLUDE_TOOLS));
    final Set<String> result =
        Arrays.stream(input.split(","))
            .filter(Objects::nonNull)
            .map(String::strip)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());

    return result;
  }

  protected Catalog getCatalog() {
    final DatabaseConnectionSource connectionSource = buildCatalogDatabaseConnectionSource();
    final SchemaCrawlerOptions schemaCrawlerOptions = getSchemaCrawlerOptions();
    final Catalog catalog = SchemaCrawlerUtility.getCatalog(connectionSource, schemaCrawlerOptions);
    return catalog;
  }

  protected McpServerTransportType getMcpTransport() {
    return transport;
  }

  protected SchemaCrawlerOptions getSchemaCrawlerOptions() {
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
      final String value = envAccessor.getenv(INFO_LEVEL);
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

    final String value = envAccessor.getenv(LOG_LEVEL);
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
  private McpServerTransportType readTransport() {
    requireNonNull(envAccessor, "No environmental accessor provided");

    final McpServerTransportType defaultValue = McpServerTransportType.stdio;

    final String value = envAccessor.getenv(MCP_SERVER_TRANSPORT);
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
