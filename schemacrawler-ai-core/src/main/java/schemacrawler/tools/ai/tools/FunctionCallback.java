/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.tools;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.StringFormat;

/**
 * Allows tools to be called. The function callback has enough context to be able to execute the
 * tool. This class can be adapted into a framework class for Spring Boot AI, LangChain4J, or other
 * library.
 */
public final class FunctionCallback<P extends FunctionParameters> {

  private static final Logger LOGGER = Logger.getLogger(FunctionCallback.class.getCanonicalName());

  private final FunctionDefinition<P> functionDefinition;
  private final Catalog catalog;

  /**
   * Function callbacks are created and registered ahead of time, with the required context that is
   * needed to run them.
   *
   * @param functionName Name of the function to execute.
   * @param catalog Database catalog.
   * @param connection A live connection to the database.
   */
  public FunctionCallback(final FunctionDefinition<P> functionDefinition, final Catalog catalog) {
    this.functionDefinition = requireNonNull(functionDefinition, "No function definition provided");
    this.catalog = catalog;
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
          Level.FINER, "Executing%n%s".formatted(toCallObject(argumentsString).toPrettyString()));
    }

    try {
      final P arguments = instantiateArguments(argumentsString);

      final FunctionReturn returnValue = executeFunction(arguments, connection);
      return returnValue;
    } catch (final Exception e) {
      LOGGER.log(
          Level.INFO,
          e,
          new StringFormat(
              "Exception executing: <%s>%n%s", toCallObject(argumentsString), e.getMessage()));
      if (e instanceof final RuntimeException runex) {
        throw runex;
      }
      throw new InternalRuntimeException(
          "Exception executing <%s>" + getFunctionName().getName(), e);
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
      objectNode.put("arguments", argumentsString);
    }

    return objectNode;
  }

  @Override
  public String toString() {
    return toCallObject(null).toPrettyString();
  }

  private FunctionReturn executeFunction(final P arguments, final Connection connection)
      throws Exception {
    requireNonNull(arguments, "No function arguments provided");

    FunctionReturn functionReturn;
    final FunctionExecutor<P> functionExecutor = functionDefinition.newExecutor();
    functionExecutor.configure(arguments);
    functionExecutor.initialize();
    functionExecutor.setCatalog(catalog);
    if (functionExecutor.usesConnection()) {
      functionExecutor.setConnection(connection);
    }
    functionReturn = functionExecutor.call();
    return functionReturn;
  }

  private P instantiateArguments(final String argumentsString) throws Exception {
    final Class<P> parametersClass = functionDefinition.getParametersClass();
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
