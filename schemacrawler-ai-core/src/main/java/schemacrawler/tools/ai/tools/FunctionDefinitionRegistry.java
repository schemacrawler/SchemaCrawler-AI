/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools;

import static us.fatehi.utility.Utility.requireNotBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.registry.BasePluginRegistry;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.StringFormat;

/** Registry for function definitions. */
public final class FunctionDefinitionRegistry extends BasePluginRegistry {

  private static final Logger LOGGER = Logger.getLogger(FunctionDefinitionRegistry.class.getName());

  private static FunctionDefinitionRegistry functionDefinitionRegistrySingleton;

  public static FunctionDefinitionRegistry getFunctionDefinitionRegistry() {
    if (functionDefinitionRegistrySingleton == null) {
      functionDefinitionRegistrySingleton = new FunctionDefinitionRegistry();
      functionDefinitionRegistrySingleton.log();
    }
    return functionDefinitionRegistrySingleton;
  }

  private static Map<String, FunctionDefinition<?>> loadFunctionDefinitionRegistry() {

    final Map<String, FunctionDefinition<?>> functionDefinitionRegistry = new HashMap<>();

    try {
      final ServiceLoader<FunctionDefinition> serviceLoader =
          ServiceLoader.load(
              FunctionDefinition.class, FunctionDefinitionRegistry.class.getClassLoader());
      for (final FunctionDefinition<?> functionDefinition : serviceLoader) {
        final String functionName = functionDefinition.getName();
        LOGGER.log(Level.FINE, new StringFormat("Loading function definition, %s", functionName));
        functionDefinitionRegistry.put(functionName, functionDefinition);
      }
    } catch (final Exception e) {
      throw new InternalRuntimeException("Could not load function definition registry", e);
    }

    LOGGER.log(
        Level.CONFIG,
        new StringFormat("Loaded %d function definitions", functionDefinitionRegistry.size()));

    return functionDefinitionRegistry;
  }

  private final Map<String, FunctionDefinition<?>> functionDefinitionRegistry;

  private FunctionDefinitionRegistry() {
    functionDefinitionRegistry = loadFunctionDefinitionRegistry();
  }

  public Collection<FunctionDefinition<?>> getFunctionDefinitions() {
    return new ArrayList<>(functionDefinitionRegistry.values());
  }

  @Override
  public String getName() {
    return "Function Definitions";
  }

  @Override
  public Collection<PropertyName> getRegisteredPlugins() {
    final Collection<PropertyName> registeredPlugins = new ArrayList<>();
    for (final FunctionDefinition<?> functionDefinition : functionDefinitionRegistry.values()) {
      registeredPlugins.add(
          new PropertyName(functionDefinition.getName(), functionDefinition.getDescription()));
    }
    return registeredPlugins;
  }

  public Collection<ToolSpecification> getToolSpecifications(final FunctionReturnType returnType) {
    final Collection<ToolSpecification> toolSpecifications = new ArrayList<>();
    for (final FunctionDefinition<?> functionDefinition : functionDefinitionRegistry.values()) {
      if (functionDefinition.getFunctionReturnType() == returnType) {
        toolSpecifications.add(ToolUtility.toToolSpecification(functionDefinition));
      }
    }
    return toolSpecifications;
  }

  public boolean hasFunctionDefinition(final String functionName) {
    return functionDefinitionRegistry.containsKey(functionName);
  }

  public Optional<FunctionDefinition<?>> lookupFunctionDefinition(final String functionName) {
    requireNotBlank(functionName, "No function name provided");

    FunctionDefinition<?> functionDefinitionToCall = null;
    for (final FunctionDefinition<?> functionDefinition :
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry().getFunctionDefinitions()) {
      if (functionDefinition.getName().equals(functionName)) {
        functionDefinitionToCall = functionDefinition;
        break;
      }
    }
    if (functionDefinitionToCall == null) {
      LOGGER.log(Level.INFO, new StringFormat("Function not found: %s", functionName));
      return Optional.empty();
    }
    return Optional.of(functionDefinitionToCall);
  }
}
