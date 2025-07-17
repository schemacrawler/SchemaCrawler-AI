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
  @DisplayName("Should allow setting and unsetting environment variables")
  void shouldAllowSettingAndUnsetting() {
    // Arrange
    final MockEnvironmentVariableAccessor accessor = new MockEnvironmentVariableAccessor();

    // Act & Assert - Setting
    accessor.setenv("VAR1", "value1");
    assertThat(accessor.getenv("VAR1"), is("value1"));

    // Act & Assert - Updating
    accessor.setenv("VAR1", "new_value");
    assertThat(accessor.getenv("VAR1"), is("new_value"));

    // Act & Assert - Unsetting
    accessor.unsetenv("VAR1");
    assertThat(accessor.getenv("VAR1"), nullValue());
  }

  @Test
  @DisplayName("Should clear all environment variables")
  void shouldClearAllVariables() {
    // Arrange
    final MockEnvironmentVariableAccessor accessor = new MockEnvironmentVariableAccessor();
    accessor.setenv("VAR1", "value1");
    accessor.setenv("VAR2", "value2");

    // Act
    accessor.clearenv();

    // Assert
    assertThat(accessor.getenv("VAR1"), nullValue());
    assertThat(accessor.getenv("VAR2"), nullValue());
  }

  @Test
  @DisplayName("Should initialize with provided environment variables")
  void shouldInitializeWithProvidedVariables() {
    // Arrange
    final Map<String, String> initialEnv = new HashMap<>();
    initialEnv.put("VAR1", "value1");
    initialEnv.put("VAR2", "value2");

    // Act
    final MockEnvironmentVariableAccessor accessor =
        new MockEnvironmentVariableAccessor(initialEnv);

    // Assert
    assertThat(accessor.getenv("VAR1"), is("value1"));
    assertThat(accessor.getenv("VAR2"), is("value2"));
  }

  @Test
  @DisplayName("Should return environment variable value if it has been set")
  void shouldReturnEnvironmentVariableValue() {
    // Arrange
    final MockEnvironmentVariableAccessor accessor = new MockEnvironmentVariableAccessor();
    accessor.setenv("TEST_VAR", "test_value");

    // Act & Assert
    assertThat(accessor.getenv("TEST_VAR"), is("test_value"));
  }

  @Test
  @DisplayName("Should return null for non-existent environment variable")
  void shouldReturnNullForNonExistentVariable() {
    // Arrange
    final MockEnvironmentVariableAccessor accessor = new MockEnvironmentVariableAccessor();

    // Act & Assert
    assertThat(accessor.getenv("NONEXISTENT_VAR"), nullValue());
  }
}
