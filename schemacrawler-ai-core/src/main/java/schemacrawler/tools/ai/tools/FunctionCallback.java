/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools;

import java.sql.Connection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schema.Catalog;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.StringFormat;

/**
 * Allows tools to be called. The function callback has enough context to be able to execute the
 * tool. This class can be adapted into a framework class for Spring Boot AI, LangChain4J, or other
 * library.
 */
public final class FunctionCallback {

  private static final Logger LOGGER = Logger.getLogger(FunctionCallback.class.getCanonicalName());

  private static ObjectMapper objectMapper = new ObjectMapper();

  private FunctionDefinition<FunctionParameters> functionDefinition;
  private final Catalog catalog;
  private final Connection connection;

  /**
   * Function callbacks are created and registered ahead of time, with the required context that is
   * needed to run them.
   *
   * @param functionName Name of the function to execute.
   * @param catalog Database catalog.
   * @param connection A live connection to the database.
   */
  public FunctionCallback(
      final String functionName, final Catalog catalog, final Connection connection) {
    this.catalog = catalog;
    this.connection = connection;

    // Look up function definition
    final Optional<FunctionDefinition<?>> lookedupFunctionDefinition =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry()
            .lookupFunctionDefinition(functionName);
    if (!lookedupFunctionDefinition.isEmpty()) {
      functionDefinition =
          (FunctionDefinition<FunctionParameters>) lookedupFunctionDefinition.get();
    } else {
      LOGGER.log(Level.WARNING, new StringFormat("Function <%s> not found", functionName));
      functionDefinition = null;
    }
  }

  /**
   * Execute the function, given arguments as a JSON string.
   *
   * @param argumentsString JSON string with arguments.
   * @return Result of execution.
   */
  public String execute(final String argumentsString) {
    final PropertyName functionName = getFunctionName();
    LOGGER.log(
        Level.INFO, new StringFormat("Executing%n%s", toObject(argumentsString).toPrettyString()));

    if (functionDefinition == null) {
      return "";
    }

    try {
      final FunctionParameters arguments = instantiateArguments(argumentsString);

      final String returnValue = executeFunction(arguments);
      return returnValue;
    } catch (final Exception e) {
      LOGGER.log(
          Level.INFO,
          e,
          new StringFormat(
              "Could not call function with arguments: %s(%s)", functionName, argumentsString));
      return e.getMessage();
    }
  }

  /**
   * Name of the function to execute.
   *
   * @return Name and description of the function.
   */
  public PropertyName getFunctionName() {
    final PropertyName functionName;
    if (functionDefinition == null) {
      functionName = new PropertyName("unknown-function");
    } else {
      functionName = functionDefinition.getFunctionName();
    }
    return functionName;
  }

  @Override
  public String toString() {
    return toObject(null).toPrettyString();
  }

  private String executeFunction(final FunctionParameters arguments) {
    requireNonNull(arguments, "No function arguments provided");

    try {
      FunctionReturn functionReturn;
      final FunctionExecutor<FunctionParameters> functionExecutor =
          functionDefinition.newExecutor();
      functionExecutor.configure(arguments);
      functionExecutor.initialize();
      functionExecutor.setCatalog(catalog);
      if (functionExecutor.usesConnection()) {
        functionExecutor.setConnection(connection);
      }
      functionReturn = functionExecutor.call();
      final String returnValue = functionReturn.get();
      return returnValue;
    } catch (final Exception e) {
      LOGGER.log(
          Level.INFO,
          e,
          new StringFormat(
              "Could not call function with arguments: %s(%s)",
              functionDefinition.getName(), arguments));
      return e.getMessage();
    }
  }

  private <P extends FunctionParameters> P instantiateArguments(final String argumentsString)
      throws Exception {
    final Class<P> parametersClass = (Class<P>) functionDefinition.getParametersClass();
    final String functionArguments;
    if (isBlank(argumentsString)) {
      functionArguments = "{}";
    } else {
      functionArguments = argumentsString;
    }
    final ObjectMapper objectMapper = new ObjectMapper();
    try {
      final P parameters = objectMapper.readValue(functionArguments, parametersClass);
      LOGGER.log(Level.FINE, String.valueOf(parameters));
      return parameters;
    } catch (final Exception e) {
      LOGGER.log(
          Level.INFO,
          e,
          new StringFormat(
              "Function parameters could not be instantiated: %s(%s)",
              parametersClass.getName(), functionArguments));
      return parametersClass.getDeclaredConstructor().newInstance();
    }
  }

  private ObjectNode toObject(final String argumentsString) {
    final ObjectNode objectNode = objectMapper.createObjectNode();

    final PropertyName functionName = getFunctionName();
    objectNode.put("name", functionName.getName());
    objectNode.put("description", functionName.getDescription());

    try {
      final String functionArguments;
      if (isBlank(argumentsString)) {
        functionArguments = "{}";
      } else {
        functionArguments = argumentsString;
      }
      final JsonNode arguments = objectMapper.readTree(functionArguments);
      objectNode.set("arguments", arguments);
    } catch (final Exception e) {
      objectNode.set("arguments", objectMapper.createObjectNode());
    }

    return objectNode;
  }
}
