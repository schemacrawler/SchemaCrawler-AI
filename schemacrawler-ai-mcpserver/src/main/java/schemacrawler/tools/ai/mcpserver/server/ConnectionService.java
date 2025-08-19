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

/**
 * A singleton service that provides access to database connection resources. This class follows an
 * initialization-on-demand pattern where the service must be explicitly initialized once before it
 * can be used.
 */
public class ConnectionService {

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
    synchronized (lock) {
      if (instance != null) {
        throw new IllegalStateException("ConnectionService has already been initialized");
      }
      instance = new ConnectionService(connection);
    }
  }

  private final Connection connection;

  /**
   * Private constructor to prevent direct instantiation. Use {@link #instantiate(Connection)} to
   * initialize and {@link #getInstance()} to access the singleton instance.
   */
  private ConnectionService(final Connection connection) {
    this.connection = requireNonNull(connection, "No connection provided");
  }

  public Connection connection() {
    return connection;
  }
}
