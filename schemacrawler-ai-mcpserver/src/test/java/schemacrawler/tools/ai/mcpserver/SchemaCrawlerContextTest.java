/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.ai.mcpserver.utility.DatabaseConnectionSourceUtility;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@DisplayName("SchemaCrawler configuration tests")
public class SchemaCrawlerContextTest {

  private Config envAccessor;
  private SchemaCrawlerContext context;

  @BeforeEach
  void setUp() {
    envAccessor = ConfigUtility.newConfig();
  }

  @Test
  @DisplayName("Should build an operations database connection source")
  void shouldBuildOperationsDatabaseConnectionSource() throws Exception {
    envAccessor.put("SCHCRWLR_JDBC_URL", "jdbc:hsqldb:mem:operations_source");
    context = new SchemaCrawlerContext(envAccessor);

    try (DatabaseConnectionSource connectionSource =
            context.buildOperationsDatabaseConnectionSource();
        Connection connection = connectionSource.get()) {
      assertThat(connection.isValid(1), is(true));
    }
  }

  @Test
  @DisplayName("Should use the configured connection source for an online catalog")
  void shouldBuildOnlineCatalogDatabaseConnectionSource() throws Exception {
    envAccessor.put("SCHCRWLR_JDBC_URL", "jdbc:hsqldb:mem:online_catalog_source");
    context = new SchemaCrawlerContext(envAccessor);

    try (DatabaseConnectionSource connectionSource =
            context.buildCatalogDatabaseConnectionSource();
        Connection connection = connectionSource.get()) {
      assertThat(connection.isValid(1), is(true));
    }
  }

  @Test
  @DisplayName("Should build an offline catalog connection source when a snapshot is configured")
  void shouldBuildOfflineCatalogDatabaseConnectionSource(@TempDir final Path tempDirectory)
      throws Exception {
    final Path snapshot = tempDirectory.resolve("catalog.ser");
    Files.writeString(snapshot, "offline catalog snapshot");
    envAccessor.put("SCHCRWLR_OFFLINE_DATABASE", snapshot.toString());
    context = new SchemaCrawlerContext(envAccessor);

    try (DatabaseConnectionSource connectionSource =
        context.buildCatalogDatabaseConnectionSource()) {
      assertTrue(DatabaseConnectionSourceUtility.isOffline(connectionSource));
    }
  }

  @Test
  @DisplayName("Should load the catalog from the configured database")
  void shouldLoadCatalog() throws Exception {
    envAccessor.put("SCHCRWLR_JDBC_URL", "jdbc:hsqldb:mem:loaded_catalog");
    context = new SchemaCrawlerContext(envAccessor);

    try (DatabaseConnectionSource connectionSource =
            context.buildOperationsDatabaseConnectionSource();
        Connection connection = connectionSource.get();
        Statement statement = connection.createStatement()) {
      statement.execute("CREATE TABLE context_test_table (id INTEGER)");

      final Catalog catalog = context.loadCatalog();

      assertThat(catalog, notNullValue());
      assertTrue(
          catalog.getTables().stream()
              .anyMatch(table -> "CONTEXT_TEST_TABLE".equalsIgnoreCase(table.getName())));
    }
  }

  @Test
  @DisplayName("Should build SchemaCrawler options when context is created")
  void shouldBuildSchemaCrawlerOptions() {
    // Arrange
    envAccessor.put("SCHCRWLR_INFO_LEVEL", "detailed");
    context = new SchemaCrawlerContext(envAccessor);

    // Act
    final SchemaCrawlerOptions options = context.schemaCrawlerOptions();

    // Assert
    assertThat(options, notNullValue());
    assertThat(options.loadOptions(), notNullValue());
    assertThat(options.limitOptions(), notNullValue());
    assertThat(options.loadOptions().schemaInfoLevel().getTag(), is("detailed"));
  }

  @Test
  @DisplayName("Should read trimmed database alias and description when set")
  void shouldReadDatabaseAliasAndDescription() {
    envAccessor.put("SCHCRWLR_DATABASE_ALIAS", "  crm-prod ");
    envAccessor.put("SCHCRWLR_DATABASE_DESCRIPTION", " CRM system of record ");
    context = new SchemaCrawlerContext(envAccessor);

    assertThat(context.databaseAlias(), is("crm-prod"));
    assertThat(context.databaseDescription(), is("CRM system of record"));
  }

  @Test
  @DisplayName("Should return empty database alias and description when unset or blank")
  void shouldReturnEmptyDatabaseAliasAndDescriptionWhenUnsetOrBlank() {
    // Unset
    context = new SchemaCrawlerContext(envAccessor);
    assertThat(context.databaseAlias(), is(""));
    assertThat(context.databaseDescription(), is(""));

    // Whitespace
    envAccessor.put("SCHCRWLR_DATABASE_ALIAS", "   \t ");
    envAccessor.put("SCHCRWLR_DATABASE_DESCRIPTION", " \n ");
    context = new SchemaCrawlerContext(envAccessor);
    assertThat(context.databaseAlias(), is(""));
    assertThat(context.databaseDescription(), is(""));
  }

  @Test
  @DisplayName("Should handle invalid JSON in SCHCRWLR_ADDITIONAL_CONFIG gracefully")
  void shouldHandleInvalidAdditionalConfigJson() {
    envAccessor.put("SCHCRWLR_ADDITIONAL_CONFIG", "this-is-not-json");
    context = new SchemaCrawlerContext(envAccessor);

    final schemacrawler.tools.options.Config config = context.readAdditionalConfig();
    // Should fall back to empty config
    assertThat(config.getStringValue("any", "default"), is("default"));
  }

  @Test
  @DisplayName("Should parse valid JSON from SCHCRWLR_ADDITIONAL_CONFIG into Config")
  void shouldParseValidAdditionalConfigJson() {
    final String json =
        """
        {
          "transport": "http",
          "exclude-tools": "tool1,tool2",
          "custom-key": "custom-value"
        }\
        """;
    envAccessor.put("SCHCRWLR_ADDITIONAL_CONFIG", json);
    context = new SchemaCrawlerContext(envAccessor);

    final schemacrawler.tools.options.Config config = context.readAdditionalConfig();
    assertThat(config.getStringValue("transport", ""), is("http"));
    assertThat(config.getStringValue("exclude-tools", ""), is("tool1,tool2"));
    assertThat(config.getStringValue("custom-key", ""), is("custom-value"));
  }

  @Test
  @DisplayName("Should read info level with custom values when environment variables are set")
  void shouldReadInfoLevelWithCustomValues() {
    // Arrange
    envAccessor.put("SCHCRWLR_INFO_LEVEL", "detailed");
    context = new SchemaCrawlerContext(envAccessor);

    // Act
    final InfoLevel infoLevel = context.readInfoLevel();

    // Assert
    assertThat(infoLevel, is(InfoLevel.detailed));
  }

  @Test
  @DisplayName("Should read info level with default values when environment variables are not set")
  void shouldReadInfoLevelWithDefaults() {
    // Arrange
    envAccessor.put("SCHCRWLR_INFO_LEVEL", null);
    context = new SchemaCrawlerContext(envAccessor);

    // Act
    final InfoLevel infoLevel = context.readInfoLevel();

    // Assert
    assertThat(infoLevel, is(InfoLevel.standard));
  }

  @Test
  @DisplayName("Should return empty additional config when unset or blank")
  void shouldReturnEmptyAdditionalConfigWhenUnsetOrBlank() {
    // Unset
    envAccessor.put("SCHCRWLR_ADDITIONAL_CONFIG", null);
    context = new SchemaCrawlerContext(envAccessor);
    assertThat(context.readAdditionalConfig().getStringValue("some-key", "default"), is("default"));

    // Empty
    envAccessor.put("SCHCRWLR_ADDITIONAL_CONFIG", "");
    context = new SchemaCrawlerContext(envAccessor);
    assertThat(context.readAdditionalConfig().getStringValue("some-key", "default"), is("default"));

    // Whitespace
    envAccessor.put("SCHCRWLR_ADDITIONAL_CONFIG", "   \t  \n  ");
    context = new SchemaCrawlerContext(envAccessor);
    assertThat(context.readAdditionalConfig().getStringValue("some-key", "default"), is("default"));
  }

  @Test
  @DisplayName("Should validate info levels correctly")
  void shouldValidateInfoLevels() {
    // Test standard level
    envAccessor.put("SCHCRWLR_INFO_LEVEL", "standard");
    context = new SchemaCrawlerContext(envAccessor);
    assertThat(context.readInfoLevel(), is(InfoLevel.standard));

    // Test detailed level
    envAccessor.put("SCHCRWLR_INFO_LEVEL", "detailed");
    context = new SchemaCrawlerContext(envAccessor);
    assertThat(context.readInfoLevel(), is(InfoLevel.detailed));

    // Test maximum level
    envAccessor.put("SCHCRWLR_INFO_LEVEL", "maximum");
    context = new SchemaCrawlerContext(envAccessor);
    assertThat(context.readInfoLevel(), is(InfoLevel.maximum));

    // Test null defaults to standard
    envAccessor.put("SCHCRWLR_INFO_LEVEL", null);
    context = new SchemaCrawlerContext(envAccessor);
    assertThat(context.readInfoLevel(), is(InfoLevel.standard));

    // Test empty string defaults to standard
    envAccessor.put("SCHCRWLR_INFO_LEVEL", "");
    context = new SchemaCrawlerContext(envAccessor);
    assertThat(context.readInfoLevel(), is(InfoLevel.standard));

    // Test invalid value defaults to standard
    envAccessor.put("SCHCRWLR_INFO_LEVEL", "invalid");
    context = new SchemaCrawlerContext(envAccessor);
    assertThat(context.readInfoLevel(), is(InfoLevel.standard));
  }
}
