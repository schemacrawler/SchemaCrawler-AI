/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.ai.mcpserver.test.MockEnvironmentVariableMap;

@DisplayName("SchemaCrawler configuration tests")
public class SchemaCrawlerContextTest {

  private MockEnvironmentVariableMap envAccessor;
  private SchemaCrawlerContext context;

  @BeforeEach
  void setUp() {
    envAccessor = new MockEnvironmentVariableMap();
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
        "{\n"
            + "  \"transport\": \"http\",\n"
            + "  \"exclude-tools\": \"tool1,tool2\",\n"
            + "  \"custom-key\": \"custom-value\"\n"
            + "}";
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

  @Test
  @DisplayName("Should validate log levels correctly")
  void shouldValidateLogLevels() {
    // Test INFO level
    envAccessor.put("SCHCRWLR_LOG_LEVEL", "INFO");
    context = new SchemaCrawlerContext(envAccessor);
    assertThat(context.readLogLevel().getName(), is("INFO"));

    // Test WARNING level
    envAccessor.put("SCHCRWLR_LOG_LEVEL", "WARNING");
    context = new SchemaCrawlerContext(envAccessor);
    assertThat(context.readLogLevel().getName(), is("WARNING"));

    // Test SEVERE level
    envAccessor.put("SCHCRWLR_LOG_LEVEL", "SEVERE");
    context = new SchemaCrawlerContext(envAccessor);
    assertThat(context.readLogLevel().getName(), is("SEVERE"));

    // Test FINE level
    envAccessor.put("SCHCRWLR_LOG_LEVEL", "FINE");
    context = new SchemaCrawlerContext(envAccessor);
    assertThat(context.readLogLevel().getName(), is("FINE"));

    // Test null defaults to INFO
    envAccessor.put("SCHCRWLR_LOG_LEVEL", null);
    context = new SchemaCrawlerContext(envAccessor);
    assertThat(context.readLogLevel().getName(), is("INFO"));

    // Test empty string defaults to INFO
    envAccessor.put("SCHCRWLR_LOG_LEVEL", "");
    context = new SchemaCrawlerContext(envAccessor);
    assertThat(context.readLogLevel().getName(), is("INFO"));

    // Test invalid value defaults to INFO
    envAccessor.put("SCHCRWLR_LOG_LEVEL", "invalid");
    context = new SchemaCrawlerContext(envAccessor);
    assertThat(context.readLogLevel().getName(), is("INFO"));
  }
}
