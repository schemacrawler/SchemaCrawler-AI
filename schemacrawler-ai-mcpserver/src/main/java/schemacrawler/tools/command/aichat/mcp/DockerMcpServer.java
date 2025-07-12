/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.mcp;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.tools.commandline.command.AvailableServers;
import us.fatehi.utility.LoggingConfig;

/**
 * Construct SchemaCrawler arguments from environment variables and run SchemaCrawler MCP Server.
 */
public class DockerMcpServer {

  /** Inner class that handles the MCP server setup. */
  public static final class McpServerContext {

    /**
     * Adds database credentials to the arguments list.
     *
     * @param arguments The list of arguments to add to
     */
    public void addDatabaseCredentials(final List<String> arguments) {
      addNonBlankArgument(arguments, "--user:env", "SCHCRWLR_DATABASE_USER");
      addNonBlankArgument(arguments, "--password:env", "SCHCRWLR_DATABASE_PASSWORD");
    }

    /**
     * Adds JDBC URL connection to the arguments list.
     *
     * @param arguments The list of arguments to add to
     */
    public void addJdbcUrlConnection(final List<String> arguments) {
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
    public void addNonBlankArgument(
        final List<String> arguments, final String argName, final String envVar) {
      final String value = System.getenv(envVar);
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
    public void addSchemaCrawlerArguments(final List<String> arguments) {
      final String infoLevel = System.getenv("SCHCRWLR_INFO_LEVEL");
      arguments.add("--info-level");
      if (isValidInfoLevel(infoLevel)) {
        arguments.add(infoLevel);
      } else {
        arguments.add(InfoLevel.standard.name());
      }

      // Add additional SchemaCrawler command line arguments
      arguments.add("--log-level");
      arguments.add(Level.OFF.getName());

      arguments.add("--routines");
      arguments.add(".*");

      arguments.add("--command");
      arguments.add("mcpserver");

      arguments.add("--transport");
      arguments.add("stdio");
    }

    /**
     * Adds server connection arguments to the arguments list.
     *
     * @param arguments The list of arguments to add to
     */
    public void addServerConnection(final List<String> arguments) {
      final String server = System.getenv("SCHCRWLR_SERVER");
      if (!isBlank(server) && isValidDatabasePlugin(server)) {
        arguments.add("--server");
        arguments.add(server);
      }

      addNonBlankArgument(arguments, "--host", "SCHCRWLR_HOST");

      final String port = System.getenv("SCHCRWLR_PORT");
      if (isNumeric(port)) {
        arguments.add("--port");
        arguments.add(port);
      }

      final String database = System.getenv("SCHCRWLR_DATABASE");
      if (!isBlank(database)) {
        arguments.add("--database");
        arguments.add(database);
      }
    }

    /**
     * Builds the complete argument list from environment variables.
     *
     * @return List of command line arguments
     */
    public String[] buildArguments() {
      final List<String> arguments = new ArrayList<>();

      // Handle connections with either JDBC URL or server/host/port
      final String jdbcUrl = System.getenv("SCHCRWLR_JDBC_URL");
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
     * Checks if a string is a valid numeric value.
     *
     * @param value The string to check
     * @return true if the string is a valid numeric value, false otherwise
     */
    public boolean isNumeric(final String value) {
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
    public boolean isValidDatabasePlugin(final String server) {
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
    public boolean isValidInfoLevel(final String infoLevel) {
      if (isBlank(infoLevel)) {
        return false;
      }
      try {
        return InfoLevel.valueOfFromString(infoLevel) != InfoLevel.unknown;
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

    new LoggingConfig(Level.OFF);

    final McpServerContext runner = new McpServerContext();
    final String[] arguments = runner.buildArguments();
    schemacrawler.Main.main(arguments);
  }
}
