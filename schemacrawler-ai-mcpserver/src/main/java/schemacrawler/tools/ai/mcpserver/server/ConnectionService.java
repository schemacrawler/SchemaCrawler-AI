/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver.server;

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.tools.databaseconnector.EnvironmentalDatabaseConnectionSourceBuilder;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

/**
 * A singleton service that provides access to database connection resources. This class follows an
 * initialization-on-demand pattern where the service must be explicitly initialized once before it
 * can be used.
 */
public class ConnectionService {

  private static final Logger LOGGER = Logger.getLogger(ConnectionService.class.getName());

  private static final Object lock = new Object();
  private static volatile ConnectionService instance;

  /**
   * Gets the singleton instance of ConnectionService. The service must have been initialized with
   * {@link #instantiate(Connection)} before this method is called.
   *
   * @return The singleton ConnectionService instance
   * @throws IllegalStateException if the service has not been initialized
   */
  public static ConnectionService getInstance() {
    if (instance == null) {
      throw new IllegalStateException("ConnectionService has not been initialized yet");
    }
    return instance;
  }

  /**
   * Initializes the ConnectionService singleton. This method should be called exactly once during
   * application startup. Subsequent calls will throw an IllegalStateException.
   *
   * @param connection SQL connection
   * @throws IllegalStateException if the service has already been initialized
   */
  public static void instantiate(final Connection connection) {
    final DatabaseConnectionSource dbConnectionSource = newDatabaseConnectionSource(connection);
    instantiate(dbConnectionSource);
  }

  /**
   * Initializes the ConnectionService singleton. This method should be called exactly once during
   * application startup. Subsequent calls will throw an IllegalStateException.
   *
   * @param dbConnectionSource Database connection sources
   * @throws IllegalStateException if the service has already been initialized
   */
  public static void instantiate(final DatabaseConnectionSource dbConnectionSource) {
    synchronized (lock) {
      if (instance != null) {
        throw new IllegalStateException("ConnectionService has already been initialized");
      }
      instance = new ConnectionService(dbConnectionSource);
    }
  }

  public static boolean isInstantiated() {
    return instance != null;
  }

  private static DatabaseConnectionSource newDatabaseConnectionSource(final Connection connection) {
    DatabaseConnectionSource dbConnectionSource = null;

    try {
      dbConnectionSource = EnvironmentalDatabaseConnectionSourceBuilder.builder().build();
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Cannot make a database connection", e);
      // Fall back to using the provided connection
      dbConnectionSource = DatabaseConnectionSources.fromConnection(connection);
    }
    return dbConnectionSource;
  }

  private final DatabaseConnectionSource dbConnectionSource;

  /**
   * Private constructor to prevent direct instantiation. Use {@link #instantiate(Connection)} to
   * initialize and {@link #getInstance()} to access the singleton instance.
   */
  private ConnectionService(final DatabaseConnectionSource dbConnectionSource) {
    this.dbConnectionSource =
        requireNonNull(dbConnectionSource, "No database connection source provided");
  }

  public Connection connection() {
    return dbConnectionSource.get();
  }
}
