/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

/**
 * Thread-safe singleton configuration manager for SchemaCrawler AI. Manages configuration settings
 * like isDryRun.
 */
public class ConfigurationManager {

  private static final Object lock = new Object();
  private static volatile ConfigurationManager instance;

  public static ConfigurationManager getInstance() {
    if (instance == null) {
      synchronized (lock) {
        if (instance == null) {
          instance = new ConfigurationManager();
        }
      }
    }
    return instance;
  }

  private boolean isDryRun = false;

  private ConfigurationManager() {
    // Private constructor to prevent direct instantiation
  }

  public boolean isDryRun() {
    return isDryRun;
  }

  public void setDryRun(final boolean dryRun) {
    isDryRun = dryRun;
  }
}
