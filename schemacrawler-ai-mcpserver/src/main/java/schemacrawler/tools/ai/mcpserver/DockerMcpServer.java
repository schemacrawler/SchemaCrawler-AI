/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.tools.command.mcpserver.McpServerTransportType;
import schemacrawler.tools.commandline.command.AvailableServers;

/**
 * Construct SchemaCrawler arguments from environment variables and run SchemaCrawler MCP Server.
 */
public class DockerMcpServer {

  /** Inner class that handles the MCP server setup. */
  public static final class McpServerContext {

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

      // Handle connections with either JDBC URL or server/host/port
      final String jdbcUrl = envAccessor.getenv("SCHCRWLR_JDBC_URL");
      if (!isBlank(jdbcUrl)) {
        addJdbcUrlConnection(arguments);
      } else {
        addServerConnection(arguments);
      }

      addDatabaseCredentials(arguments);
      addSchemaCrawlerArguments(arguments);

      return arguments.toArray(new String[0]);
    }

    /**
     * Adds database credentials to the arguments list.
     *
     * @param arguments The list of arguments to add to
     */
    protected void addDatabaseCredentials(final List<String> arguments) {
      addNonBlankArgument(arguments, "--user:env", "SCHCRWLR_DATABASE_USER");
      addNonBlankArgument(arguments, "--password:env", "SCHCRWLR_DATABASE_PASSWORD");
    }

    /**
     * Adds JDBC URL connection to the arguments list.
     *
     * @param arguments The list of arguments to add to
     */
    protected void addJdbcUrlConnection(final List<String> arguments) {
      addNonBlankArgument(arguments, "--url", "SCHCRWLR_JDBC_URL");
    }

    /**
     * Adds an argument and its value to the arguments list if the corresponding environment
     * variable is not blank.
     *
     * @param arguments The list of arguments to add to
     * @param argName The name of the argument (e.g., "--user")
     * @param envVar The name of the environment variable
     */
    protected void addNonBlankArgument(
        final List<String> arguments, final String argName, final String envVar) {
      final String value = envAccessor.getenv(envVar);
      if (!isBlank(value)) {
        arguments.add(argName);
        arguments.add(value);
      }
    }

    /**
     * Adds SchemaCrawler specific arguments to the arguments list.
     *
     * @param arguments The list of arguments to add to
     */
    protected void addSchemaCrawlerArguments(final List<String> arguments) {
      final String infoLevel = envAccessor.getenv("SCHCRWLR_INFO_LEVEL");
      arguments.add("--info-level");
      if (isValidInfoLevel(infoLevel)) {
        arguments.add(infoLevel);
      } else {
        arguments.add(InfoLevel.standard.name());
      }

      final String logLevel = envAccessor.getenv("SCHCRWLR_LOG_LEVEL");
      arguments.add("--log-level");
      if (isValidLogLevel(logLevel)) {
        arguments.add(logLevel);
      } else {
        arguments.add(Level.INFO.getName());
      }

      arguments.add("--routines");
      arguments.add(".*");

      arguments.add("--command");
      arguments.add("mcpserver");

      arguments.add("--transport");
      arguments.add(McpServerTransportType.stdio.name());
    }

    /**
     * Adds server connection arguments to the arguments list.
     *
     * @param arguments The list of arguments to add to
     */
    protected void addServerConnection(final List<String> arguments) {
      final String server = envAccessor.getenv("SCHCRWLR_SERVER");
      if (!isBlank(server) && isValidDatabasePlugin(server)) {
        arguments.add("--server");
        arguments.add(server);
      }

      addNonBlankArgument(arguments, "--host", "SCHCRWLR_HOST");

      final String port = envAccessor.getenv("SCHCRWLR_PORT");
      if (isNumeric(port)) {
        arguments.add("--port");
        arguments.add(port);
      }

      final String database = envAccessor.getenv("SCHCRWLR_DATABASE");
      if (!isBlank(database)) {
        arguments.add("--database");
        arguments.add(database);
      }
    }

    /**
     * Checks if a string is a valid numeric value.
     *
     * @param value The string to check
     * @return true if the string is a valid numeric value, false otherwise
     */
    protected boolean isNumeric(final String value) {
      if (isBlank(value)) {
        return false;
      }
      try {
        Integer.parseInt(value);
        return true;
      } catch (final NumberFormatException e) {
        return false;
      }
    }

    /**
     * Checks if a string is a valid SchemaCrawler database plugin.
     *
     * @param server The server string to check
     * @return true if the string might be a valid database plugin, false otherwise
     */
    protected boolean isValidDatabasePlugin(final String server) {
      if (isBlank(server)) {
        return false;
      }
      final AvailableServers servers = new AvailableServers();
      for (final String availableServer : servers) {
        if (availableServer.equals(server)) {
          return true;
        }
      }
      return false;
    }

    /**
     * Checks if a string is a valid SchemaCrawler info level.
     *
     * @param infoLevel The info level string to check
     * @return true if the string is a valid info level, false otherwise
     */
    protected boolean isValidInfoLevel(final String infoLevel) {
      if (isBlank(infoLevel)) {
        return false;
      }
      try {
        return InfoLevel.valueOfFromString(infoLevel) != InfoLevel.unknown;
      } catch (final Exception e) {
        return false;
      }
    }

    /**
     * Checks if a string is a valid log level.
     *
     * @param logLevel The log level string to check
     * @return true if the string is a valid info level, false otherwise
     */
    protected boolean isValidLogLevel(final String logLevel) {
      if (isBlank(logLevel)) {
        return false;
      }
      try {
        Level.parse(logLevel);
        return true;
      } catch (final Exception e) {
        return false;
      }
    }
  }

  /**
   * Main method that reads environment variables, constructs arguments, and runs SchemaCrawler MCP
   * Server.
   *
   * @param args Command line arguments (will be combined with environment variable arguments)
   * @throws Exception If an error occurs during execution
   */
  public static void main(final String[] args) throws Exception {
    final McpServerContext runner = new McpServerContext();
    final String[] arguments = runner.buildArguments();
    schemacrawler.Main.main(arguments);
  }
}
