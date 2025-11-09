/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import us.fatehi.utility.readconfig.EnvironmentVariableConfig;

/**
 * Mock implementation of EnvironmentVariableAccessor for testing. Allows setting and getting
 * environment variables without affecting the actual system environment.
 */
public final class MockEnvironmentVariableMap implements EnvironmentVariableConfig {

  private final Map<String, String> environmentVariables;

  /** Creates a new instance with an empty environment. */
  public MockEnvironmentVariableMap() {
    environmentVariables = new HashMap<>();
  }

  /**
   * Creates a new instance with the specified environment variables.
   *
   * @param variables initial environment variables
   */
  public MockEnvironmentVariableMap(final Map<String, String> variables) {
    environmentVariables = new HashMap<>(variables);
  }

  @Override
  public Map<String, String> getenv() {
    return Collections.unmodifiableMap(environmentVariables);
  }

  /**
   * Sets an environment variable.
   *
   * @param name the name of the environment variable
   * @param value the value of the environment variable
   * @return the previous value of the environment variable, or null if it did not have one
   */
  public String put(final String name, final String value) {
    return environmentVariables.put(name, value);
  }
}
