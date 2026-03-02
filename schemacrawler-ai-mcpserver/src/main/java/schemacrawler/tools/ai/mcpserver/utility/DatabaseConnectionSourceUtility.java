/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.utility;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.tools.offline.jdbc.OfflineConnection;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.database.DatabaseUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@UtilityMarker
public final class DatabaseConnectionSourceUtility {

  private static final Logger LOGGER =
      Logger.getLogger(DatabaseConnectionSourceUtility.class.getName());

  public static boolean canConnect(final DatabaseConnectionSource connectionSource) {
    if (connectionSource == null) {
      return false;
    }

    try (final Connection connection = connectionSource.get(); ) {
      DatabaseUtility.checkConnection(connection);
      return true;
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not obtain a database connection", e);
      return false;
    }
  }

  public static boolean isOffline(final DatabaseConnectionSource connectionSource) {
    if (connectionSource == null || !canConnect(connectionSource)) {
      return true;
    }

    try (final Connection connection = connectionSource.get(); ) {
      return connection.unwrap(Connection.class) instanceof OfflineConnection;
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not check for offline connection", e);
      return false;
    }
  }

  private DatabaseConnectionSourceUtility() {
    // Prevent instantiation
  }
}
