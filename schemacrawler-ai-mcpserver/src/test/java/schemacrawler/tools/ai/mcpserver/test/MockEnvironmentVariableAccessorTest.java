/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MockEnvironmentVariableAccessor tests")
public class MockEnvironmentVariableAccessorTest {

  @Test
  @DisplayName("Should allow setting environment variables")
  void shouldAllowSettingAndUnsetting() {
    // Arrange
    final MockEnvironmentVariableMap accessor = new MockEnvironmentVariableMap();

    // Act & Assert - Setting
    accessor.put("VAR1", "value1");
    assertThat(accessor.getStringValue("VAR1", null), is("value1"));

    // Act & Assert - Updating
    accessor.put("VAR1", "new_value");
    assertThat(accessor.getStringValue("VAR1", null), is("new_value"));
  }

  @Test
  @DisplayName("Should initialize with provided environment variables")
  void shouldInitializeWithProvidedVariables() {
    // Arrange
    final Map<String, String> initialEnv = new HashMap<>();
    initialEnv.put("VAR1", "value1");
    initialEnv.put("VAR2", "value2");

    // Act
    final MockEnvironmentVariableMap accessor = new MockEnvironmentVariableMap(initialEnv);

    // Assert
    assertThat(accessor.getStringValue("VAR1", null), is("value1"));
    assertThat(accessor.getStringValue("VAR2", null), is("value2"));
  }

  @Test
  @DisplayName("Should return environment variable value if it has been set")
  void shouldReturnEnvironmentVariableValue() {
    // Arrange
    final MockEnvironmentVariableMap accessor = new MockEnvironmentVariableMap();
    accessor.put("TEST_VAR", "test_value");

    // Act & Assert
    assertThat(accessor.getStringValue("TEST_VAR", null), is("test_value"));
  }

  @Test
  @DisplayName("Should return null for non-existent environment variable")
  void shouldReturnNullForNonExistentVariable() {
    // Arrange
    final MockEnvironmentVariableMap accessor = new MockEnvironmentVariableMap();

    // Act & Assert
    assertThat(accessor.getStringValue("NONEXISTENT_VAR", null), nullValue());
  }
}
