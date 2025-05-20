package schemacrawler.tools.command.aichat.mcp;

import schemacrawler.schema.Catalog;

import java.sql.Connection;

import static java.util.Objects.requireNonNull;


/**
 * A singleton service that provides access to database connection resources.
 * This class follows an initialization-on-demand pattern where the service
 * must be explicitly initialized once before it can be used.
 */
public class ConnectionService {

  private static final Object lock = new Object();
  private static volatile ConnectionService connectionService;
  private final Catalog catalog;
  private final Connection connection;
  private final McpServerCommandOptions options;

  /**
   * Private constructor to prevent direct instantiation.
   * Use {@link #instantiate(McpServerCommandOptions, Catalog, Connection)} to initialize
   * and {@link #getInstance()} to access the singleton instance.
   */
  private ConnectionService(final McpServerCommandOptions options, final Catalog catalog, final Connection connection) {
    this.catalog = requireNonNull(catalog, "No catalog provided");
    this.connection = requireNonNull(connection, "No connection provided");
    this.options = requireNonNull(options, "No options provided");
  }

  /**
   * Initializes the ConnectionService singleton.
   * This method should be called exactly once during application startup.
   * Subsequent calls will throw an IllegalStateException.
   *
   * @param options Command options
   * @param catalog Database schema catalog
   * @param connection SQL connection
   * @throws IllegalStateException if the service has already been initialized
   */
  public static void instantiate(final McpServerCommandOptions options, final Catalog catalog, final Connection connection) {
    synchronized (lock) {
      if (connectionService != null) {
        throw new IllegalStateException("ConnectionService has already been initialized");
      }
      connectionService = new ConnectionService(options, catalog, connection);
    }
  }

  /**
   * Gets the singleton instance of ConnectionService.
   * The service must have been initialized with {@link #instantiate(McpServerCommandOptions, Catalog, Connection)}
   * before this method is called.
   *
   * @return The singleton ConnectionService instance
   * @throws IllegalStateException if the service has not been initialized
   */
  public static ConnectionService getInstance() {
    if (connectionService == null) {
      throw new IllegalStateException("ConnectionService has not been initialized yet");
    }
    return connectionService;
  }

  public Catalog catalog() {
    return catalog;
  }

  public Connection connection() {
    return connection;
  }

  public McpServerCommandOptions options() {
    return options;
  }
}
