/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.test;

import java.util.HashMap;
import java.util.Map;
import us.fatehi.utility.ioresource.EnvironmentVariableAccessor;

/**
 * Mock implementation of EnvironmentVariableAccessor for testing. Allows setting and getting
 * environment variables without affecting the actual system environment.
 */
public class MockEnvironmentVariableAccessor implements EnvironmentVariableAccessor {

  private final Map<String, String> environmentVariables;

  /** Creates a new instance with an empty environment. */
  public MockEnvironmentVariableAccessor() {
    environmentVariables = new HashMap<>();
  }

  /**
   * Creates a new instance with the specified environment variables.
   *
   * @param variables initial environment variables
   */
  public MockEnvironmentVariableAccessor(final Map<String, String> variables) {
    environmentVariables = new HashMap<>(variables);
  }

  /** Clears all environment variables. */
  public void clearenv() {
    environmentVariables.clear();
  }

  @Override
  public String getenv(final String name) {
    return environmentVariables.get(name);
  }

  /**
   * Sets an environment variable.
   *
   * @param name the name of the environment variable
   * @param value the value of the environment variable
   * @return the previous value of the environment variable, or null if it did not have one
   */
  public String setenv(final String name, final String value) {
    return environmentVariables.put(name, value);
  }

  /**
   * Removes an environment variable.
   *
   * @param name the name of the environment variable
   * @return the previous value of the environment variable, or null if it did not have one
   */
  public String unsetenv(final String name) {
    return environmentVariables.remove(name);
  }
}
