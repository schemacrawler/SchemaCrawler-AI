/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.tools;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElseGet;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.importance.model.ImportanceModel;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
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
  private final ERModel erModel;
  private final ImportanceModel importanceModel;

  /**
   * Function callbacks are created and registered ahead of time, with the required context that is
   * needed to run them.
   *
   * @param functionDefinition Definition of the function to execute.
   * @param catalog Database catalog.
   * @param erModel Entity-relationship model.
   * @param importanceModel Table importance model, may be null.
   */
  public FunctionCallback(
      final FunctionDefinition<P> functionDefinition,
      final Catalog catalog,
      final ERModel erModel,
      final ImportanceModel importanceModel) {
    this.functionDefinition = requireNonNull(functionDefinition, "No function definition provided");
    this.catalog = catalog;
    this.erModel = erModel;
    this.importanceModel = importanceModel;
  }

  /**
   * Execute the function, given a read-only argument tree.
   *
   * @param argumentsNode JSON arguments; null is treated as an empty object.
   * @return Result of execution.
   */
  public FunctionReturn execute(
      final JsonNode argumentsNode, final DatabaseConnectionSource connectionSource) {

    requireNonNull(connectionSource, "No database connection provided");
    final JsonNode argumentsTree = requireNonNullElseGet(argumentsNode, mapper::createObjectNode);

    if (LOGGER.isLoggable(Level.FINER)) {
      LOGGER.log(
          Level.FINER, "Executing%n%s".formatted(toCallObject(argumentsTree).toPrettyString()));
    }

    try {
      final P arguments = instantiateArguments(argumentsTree);

      final FunctionReturn returnValue = executeFunction(arguments, connectionSource);
      return returnValue;
    } catch (final Exception e) {
      LOGGER.log(
          Level.INFO,
          e,
          new StringFormat(
              "Exception executing: <%s>%n%s%n%s",
              functionDefinition.getFunctionName(), e.getMessage(), toCallObject(argumentsTree)));
      if (e instanceof final RuntimeException runex) {
        throw runex;
      }
      throw new InternalRuntimeException(
          "Exception executing <%s>".formatted(getFunctionName().getName()), e);
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

  /**
   * Describe a call - name and arguments.
   *
   * @param argumentsNode JSON arguments; null is treated as an empty object.
   * @return Function name and arguments.
   */
  public JsonNode toCallObject(final JsonNode argumentsNode) {
    final ObjectNode objectNode = mapper.createObjectNode();

    final PropertyName functionName = getFunctionName();
    objectNode.put("name", functionName.getName());
    objectNode.set("arguments", requireNonNullElseGet(argumentsNode, mapper::createObjectNode));

    return objectNode;
  }

  @Override
  public String toString() {
    return toCallObject(null).toPrettyString();
  }

  private FunctionReturn executeFunction(
      final P arguments, final DatabaseConnectionSource connectionSource) throws Exception {
    requireNonNull(arguments, "No function arguments provided");

    FunctionReturn functionReturn;
    final FunctionExecutor<P> functionExecutor = functionDefinition.newExecutor();
    functionExecutor.configure(arguments);
    functionExecutor.initialize();
    functionExecutor.setCatalog(catalog);
    functionExecutor.setERModel(erModel);
    functionExecutor.setImportanceModel(importanceModel);
    if (functionExecutor.usesConnection()) {
      functionExecutor.setConnectionSource(connectionSource);
    }
    functionReturn = functionExecutor.call();
    return functionReturn;
  }

  private P instantiateArguments(final JsonNode argumentsNode) {
    final Class<P> parametersClass = functionDefinition.getParametersClass();
    P parameters = null;
    if (parametersClass != null && argumentsNode != null) {
      try {
        parameters = mapper.treeToValue(argumentsNode, parametersClass);
      } catch (final Exception e) {
        LOGGER.log(Level.FINE, e, () -> parametersClass.getName());
      }
    }
    if (parameters == null) {
      parameters = functionDefinition.newParameters();
    }

    LOGGER.log(Level.FINE, String.valueOf(parameters));
    return parameters;
  }
}
