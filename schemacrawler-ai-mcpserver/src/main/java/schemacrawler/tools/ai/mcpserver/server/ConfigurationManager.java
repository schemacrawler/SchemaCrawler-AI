/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

import static java.util.Objects.requireNonNull;

import schemacrawler.schema.Catalog;

/**
 * Thread-safe singleton configuration manager for SchemaCrawler AI. Manages configuration settings
 * like isDryRun.
 */
public class ConfigurationManager {

  private static final Object lock = new Object();
  private static volatile ConfigurationManager instance;

  public static ConfigurationManager getInstance() {
    if (instance == null) {
      throw new IllegalStateException("ConfigurationManager has not been initialized yet");
    }
    return instance;
  }

  /**
   * Initializes the ConnectionService singleton. This method should be called exactly once during
   * application startup. Subsequent calls will throw an IllegalStateException.
   *
   * @param catalog Database schema catalog
   * @param connection SQL connection
   * @throws IllegalStateException if the service has already been initialized
   */
  public static void instantiate(final Catalog catalog) {
    synchronized (lock) {
      if (instance != null) {
        throw new IllegalStateException("ConnectionService has already been initialized");
      }
      instance = new ConfigurationManager(catalog);
    }
  }

  private boolean isDryRun = false;
  private final Catalog catalog;

  private ConfigurationManager(final Catalog catalog) {
    this.catalog = requireNonNull(catalog, "No catalog provided");
  }

  public Catalog getCatalog() {
    return catalog;
  }

  public boolean isDryRun() {
    return isDryRun;
  }

  public void setDryRun(final boolean dryRun) {
    isDryRun = dryRun;
  }
}
