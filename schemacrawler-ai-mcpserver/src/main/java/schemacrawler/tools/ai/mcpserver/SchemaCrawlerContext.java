/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptions;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.tools.databaseconnector.EnvironmentalDatabaseConnectionSourceBuilder;
import schemacrawler.tools.offline.jdbc.OfflineConnectionUtility;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import tools.jackson.databind.JsonNode;
import us.fatehi.utility.LoggingConfig;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.readconfig.EnvironmentVariableConfig;
import us.fatehi.utility.readconfig.ReadConfig;

/** Inner class that handles the MCP server setup. */
public final class SchemaCrawlerContext {

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerContext.class.getName());

  private static final String ADDITIONAL_CONFIG = "SCHCRWLR_ADDITIONAL_CONFIG";
  private static final String INFO_LEVEL = "SCHCRWLR_INFO_LEVEL";
  private static final String LOG_LEVEL = "SCHCRWLR_LOG_LEVEL";
  private static final String OFFLINE_DATABASE = "SCHCRWLR_OFFLINE_DATABASE";

  private final ReadConfig envAccessor;
  private final SchemaCrawlerOptions schemaCrawlerOptions;

  /** Default constructor that uses System.getenv */
  public SchemaCrawlerContext() {
    this((EnvironmentVariableConfig) System::getenv);
  }

  /**
   * Constructor with environment variable accessor for testing
   *
   * @param envAccessor The environment variable accessor
   */
  public SchemaCrawlerContext(final ReadConfig envAccessor) {
    this.envAccessor = requireNonNull(envAccessor, "No environment accessor provided");

    final Level logLevel = readLogLevel();
    new LoggingConfig(logLevel);

    schemaCrawlerOptions = buildSchemaCrawlerOptions();
  }

  /**
   * Builds the complete argument list from environment variables.
   *
   * @return List of command line arguments
   */
  public DatabaseConnectionSource buildOperationsDatabaseConnectionSource() {
    final DatabaseConnectionSource databaseConnectionSource =
        EnvironmentalDatabaseConnectionSourceBuilder.builder(envAccessor).build();
    return databaseConnectionSource;
  }

  public Catalog loadCatalog() {
    final DatabaseConnectionSource connectionSource = buildCatalogDatabaseConnectionSource();
    final SchemaRetrievalOptions schemaRetrievalOptions =
        SchemaCrawlerUtility.matchSchemaRetrievalOptions(connectionSource);
    final Config additionalConfig = readAdditionalConfig();
    final Catalog catalog =
        SchemaCrawlerUtility.getCatalog(
            connectionSource, schemaRetrievalOptions, schemaCrawlerOptions, additionalConfig);
    return catalog;
  }

  public SchemaCrawlerOptions schemaCrawlerOptions() {
    return schemaCrawlerOptions;
  }

  DatabaseConnectionSource buildCatalogDatabaseConnectionSource() {
    final String offlineDatabasePathString =
        trimToEmpty(envAccessor.getStringValue(OFFLINE_DATABASE, ""));
    if (isBlank(offlineDatabasePathString)) {
      return buildOperationsDatabaseConnectionSource();
    }

    final Path offlineDatabasePath = Path.of(offlineDatabasePathString);
    final DatabaseConnectionSource dbConnectionSource =
        OfflineConnectionUtility.newOfflineDatabaseConnectionSource(offlineDatabasePath);
    return dbConnectionSource;
  }

  SchemaCrawlerOptions buildSchemaCrawlerOptions() {
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

  Config readAdditionalConfig() {
    final String additionalConfigString = envAccessor.getStringValue(ADDITIONAL_CONFIG, "");
    if (isBlank(additionalConfigString)) {
      return ConfigUtility.newConfig();
    }
    try {
      final JsonNode configNode = mapper.readTree(additionalConfigString);
      final Map<String, Object> configMap = mapper.convertValue(configNode, HashMap.class);
      return ConfigUtility.fromMap(configMap);
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not load config <%s>" + additionalConfigString, e);
      return ConfigUtility.newConfig();
    }
  }

  /**
   * Parses a string and returns a valid SchemaCrawler info level.
   *
   * @param value The info level string to check
   * @return InfoLevel Non-null value
   */
  InfoLevel readInfoLevel() {

    final InfoLevel defaultValue = InfoLevel.standard;
    try {
      final String value = envAccessor.getStringValue(INFO_LEVEL, "");
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
  Level readLogLevel() {

    final Level defaultValue = Level.INFO;

    final String value = envAccessor.getStringValue(LOG_LEVEL, "");
    if (isBlank(value)) {
      return defaultValue;
    }
    try {
      return Level.parse(value);
    } catch (final Exception e) {
      return defaultValue;
    }
  }
}
