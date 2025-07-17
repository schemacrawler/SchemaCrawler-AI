/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver;

/** Implementation of EnvironmentVariableAccessor that uses System.getenv. */
public class SystemEnvironmentVariableAccessor implements EnvironmentVariableAccessor {

  @Override
  public String getenv(final String name) {
    return System.getenv(name);
  }
}
