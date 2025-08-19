package schemacrawler.tools.ai.mcpserver;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.tools.command.mcpserver.McpServerTransportType;
import schemacrawler.tools.databaseconnector.EnvironmentalDatabaseConnectionSourceBuilder;
import us.fatehi.utility.datasource.DatabaseConnectionSourceBuilder;
import us.fatehi.utility.ioresource.EnvironmentVariableAccessor;

/** Inner class that handles the MCP server setup. */
public final class McpServerContext {

  private final EnvironmentVariableAccessor envAccessor;

  /** Default constructor that uses System.getenv */
  public McpServerContext() {
    this(System::getenv);
  }

  /**
   * Constructor with environment variable accessor for testing
   *
   * @param envAccessor The environment variable accessor
   */
  protected McpServerContext(final EnvironmentVariableAccessor envAccessor) {
    this.envAccessor = requireNonNull(envAccessor, "No environment accessor provided");
  }

  /**
   * Builds the complete argument list from environment variables.
   *
   * @return List of command line arguments
   */
  public String[] buildArguments() {

    final List<String> arguments = new ArrayList<>();

    final List<String> offlineDatabaseArgs = buildOfflineDatabaseArguments();
    if (offlineDatabaseArgs.size() > 0) {
      arguments.addAll(offlineDatabaseArgs);
    } else {
      final DatabaseConnectionSourceBuilder databaseConnectionSourceBuilder =
          EnvironmentalDatabaseConnectionSourceBuilder.builder(envAccessor);
      final List<String> connectionArguments = databaseConnectionSourceBuilder.toArguments();

      arguments.addAll(connectionArguments);
    }

    addSchemaCrawlerArguments(arguments);

    return arguments.toArray(new String[0]);
  }

  /**
   * Adds SchemaCrawler specific arguments to the arguments list.
   *
   * @param arguments The list of arguments to add to
   */
  protected void addSchemaCrawlerArguments(final List<String> arguments) {
    final String infoLevel = envAccessor.getenv("SCHCRWLR_INFO_LEVEL");
    arguments.add("--info-level");
    arguments.add(validInfoLevel(infoLevel).name());

    final String logLevel = envAccessor.getenv("SCHCRWLR_LOG_LEVEL");
    arguments.add("--log-level");
    arguments.add(validLogLevel(logLevel).getName());

    arguments.add("--routines");
    arguments.add(".*");

    arguments.add("--command");
    arguments.add("mcpserver");

    arguments.add("--transport");
    arguments.add(McpServerTransportType.stdio.name());
  }

  /**
   * Checks if a string is a valid SchemaCrawler info level.
   *
   * @param infoLevelString The info level string to check
   * @return true if the string is a valid info level, false otherwise
   */
  protected InfoLevel validInfoLevel(final String infoLevelString) {
    if (isBlank(infoLevelString)) {
      return InfoLevel.standard;
    }
    try {
      InfoLevel infoLevel = InfoLevel.valueOfFromString(infoLevelString);
      if (infoLevel == InfoLevel.unknown) {
        infoLevel = InfoLevel.standard;
      }
      return infoLevel;
    } catch (final Exception e) {
      return InfoLevel.standard;
    }
  }

  /**
   * Checks if a string is a valid log level.
   *
   * @param logLevel The log level string to check
   * @return true if the string is a valid info level, false otherwise
   */
  protected Level validLogLevel(final String logLevel) {
    if (isBlank(logLevel)) {
      return Level.INFO;
    }
    try {
      return Level.parse(logLevel);
    } catch (final Exception e) {
      return Level.INFO;
    }
  }

  private List<String> buildOfflineDatabaseArguments() {

    final List<String> arguments = new ArrayList<>();

    final String offlineDatabasePathString =
        trimToEmpty(envAccessor.getenv("SCHCRWLR_OFFLINE_DATABASE"));
    if (isBlank(offlineDatabasePathString)) {
      return arguments;
    }

    final Path offlineDatabasePath = Path.of(offlineDatabasePathString);
    if (!offlineDatabasePath.toFile().exists() && !offlineDatabasePath.toFile().isFile()) {
      return arguments;
    }

    arguments.add("--server");
    arguments.add("offline");
    arguments.add("--database");
    arguments.add(offlineDatabasePath.toAbsolutePath().toString());

    return arguments;
  }
}
