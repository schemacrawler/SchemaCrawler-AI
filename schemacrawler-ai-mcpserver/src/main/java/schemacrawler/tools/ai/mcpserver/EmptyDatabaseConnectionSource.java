package schemacrawler.tools.ai.mcpserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schemacrawler.exceptions.DatabaseAccessException;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public class EmptyDatabaseConnectionSource implements DatabaseConnectionSource {

  private static final Logger LOGGER =
      Logger.getLogger(EmptyDatabaseConnectionSource.class.getName());

  @Override
  public void close() throws Exception {
    // No-op
  }

  /** Get a connection to an empty SQLite in memory database. */
  @Override
  public Connection get() {
    try {
      final Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
      return connection;
    } catch (final SQLException e) {
      final String message = "Could not create an empty connection";
      LOGGER.log(Level.WARNING, message, e);
      throw new DatabaseAccessException(message, e);
    }
  }

  @Override
  public boolean releaseConnection(final Connection connection) {
    return false;
  }

  @Override
  public void setFirstConnectionInitializer(final Consumer<Connection> connectionInitializer) {
    // No-op
  }
}
