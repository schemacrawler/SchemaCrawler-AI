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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.ai.mcpserver.SystemEnvironmentVariableAccessor;

@DisplayName("SystemEnvironmentVariableAccessor tests")
public class SystemEnvironmentVariableAccessorTest {

  @Test
  @DisplayName("Should return environment variable value if it exists")
  void shouldReturnEnvironmentVariableValue() {
    // Arrange
    final SystemEnvironmentVariableAccessor accessor = new SystemEnvironmentVariableAccessor();

    // Get the value of a well-known environment variable that should exist on all systems
    // like PATH, USER, HOME, etc. We'll check for PATH here
    final String pathValue = System.getenv("PATH");

    // Act & Assert - but only if the PATH variable actually exists
    if (pathValue != null) {
      assertThat(accessor.getenv("PATH"), is(pathValue));
    }
  }

  @Test
  @DisplayName("Should return null for non-existent environment variable")
  void shouldReturnNullForNonExistentVariable() {
    // Arrange
    final SystemEnvironmentVariableAccessor accessor = new SystemEnvironmentVariableAccessor();

    // Act & Assert - using a highly unlikely environment variable name
    assertThat(accessor.getenv("SCHEMACRAWLER_TEST_NONEXISTENT_VARIABLE_12345"), nullValue());
  }
}
