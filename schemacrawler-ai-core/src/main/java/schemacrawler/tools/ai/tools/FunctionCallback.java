/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static us.fatehi.utility.Utility.isBlank;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.sql.Connection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.functions.JsonFunctionReturn;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.StringFormat;

/**
 * Allows tools to be called. The function callback has enough context to be able to execute the
 * tool. This class can be adapted into a framework class for Spring Boot AI, LangChain4J, or other
 * library.
 */
public final class FunctionCallback {

  private static final Logger LOGGER = Logger.getLogger(FunctionCallback.class.getCanonicalName());

  private FunctionDefinition<FunctionParameters> functionDefinition;
  private final Catalog catalog;

  /**
   * Function callbacks are created and registered ahead of time, with the required context that is
   * needed to run them.
   *
   * @param functionName Name of the function to execute.
   * @param catalog Database catalog.
   * @param connection A live connection to the database.
   */
  public FunctionCallback(final String functionName, final Catalog catalog) {
    this.catalog = catalog;

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
  public FunctionReturn execute(final String argumentsString, final Connection connection) {

    requireNonNull(connection, "No database connection provided");

    if (LOGGER.isLoggable(Level.FINER)) {
      LOGGER.log(
          Level.FINER,
          String.format("Executing%n%s", toCallObject(argumentsString).toPrettyString()));
    }

    if (functionDefinition == null) {
      return new JsonFunctionReturn(mapper.missingNode());
    }

    try {
      final FunctionParameters arguments = instantiateArguments(argumentsString);

      final FunctionReturn returnValue = executeFunction(arguments, connection);
      return returnValue;
    } catch (final Exception e) {
      LOGGER.log(
          Level.INFO,
          e,
          new StringFormat(
              "Exception executing: %s%n%s", toCallObject(argumentsString), e.getMessage()));
      return new JsonFunctionReturn(e);
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

  public JsonNode toCallObject(final String argumentsString) {
    final ObjectNode objectNode = mapper.createObjectNode();

    final PropertyName functionName = getFunctionName();
    objectNode.put("name", functionName.getName());

    try {
      final String functionArguments;
      if (isBlank(argumentsString)) {
        functionArguments = "{}";
      } else {
        functionArguments = argumentsString;
      }
      final JsonNode arguments = mapper.readTree(functionArguments);
      objectNode.set("arguments", arguments);
    } catch (final Exception e) {
      objectNode.set("arguments", mapper.createObjectNode());
    }

    return objectNode;
  }

  @Override
  public String toString() {
    return toCallObject(null).toPrettyString();
  }

  private FunctionReturn executeFunction(
      final FunctionParameters arguments, final Connection connection) throws Exception {
    requireNonNull(arguments, "No function arguments provided");

    FunctionReturn functionReturn;
    final FunctionExecutor<FunctionParameters> functionExecutor = functionDefinition.newExecutor();
    functionExecutor.configure(arguments);
    functionExecutor.initialize();
    functionExecutor.setCatalog(catalog);
    if (functionExecutor.usesConnection()) {
      functionExecutor.setConnection(connection);
    }
    functionReturn = functionExecutor.call();
    return functionReturn;
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
    try {
      final P parameters = mapper.readValue(functionArguments, parametersClass);
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
}
