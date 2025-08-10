/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.tools;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.ai.functions.DescribeRoutinesFunctionDefinition;
import schemacrawler.tools.ai.functions.DescribeTablesFunctionDefinition;
import schemacrawler.tools.ai.functions.ListAcrossTablesFunctionDefinition;
import schemacrawler.tools.ai.functions.ListFunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionDefinitionRegistry;
import schemacrawler.tools.ai.tools.FunctionReturnType;
import schemacrawler.tools.ai.tools.ToolSpecification;
import us.fatehi.utility.property.PropertyName;

public class FunctionDefinitionRegistryTest {

  private static final int NUM_TEXT_FUNCTIONS = 0;
  private static final int NUM_JSON_FUNCTIONS = 4;

  @Test
  public void name() {
    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
    assertThat(registry.getName(), is("Function Definitions"));
  }

  @Test
  public void registeredPlugins() {

    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
    final Collection<PropertyName> functionDefinitions = registry.getRegisteredPlugins();

    assertThat(functionDefinitions, hasSize(NUM_TEXT_FUNCTIONS + NUM_JSON_FUNCTIONS));

    final List<String> names =
        functionDefinitions.stream().map(PropertyName::getName).collect(toList());
    assertThat(
        names,
        containsInAnyOrder("describe-tables", "describe-routines", "list", "list-across-tables"));
  }

  @Test
  public void testCommandPlugin() throws Exception {
    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
    final Collection<FunctionDefinition<?>> functions =
        registry.getFunctionDefinitions(FunctionReturnType.JSON);
    assertThat(functions, hasSize(NUM_JSON_FUNCTIONS));
    assertThat(
        functions.stream()
            .map(function -> function.getClass().getSimpleName())
            .collect(Collectors.toList()),
        containsInAnyOrder(
            DescribeTablesFunctionDefinition.class.getSimpleName(),
            DescribeRoutinesFunctionDefinition.class.getSimpleName(),
            ListFunctionDefinition.class.getSimpleName(),
            ListAcrossTablesFunctionDefinition.class.getSimpleName()));
  }

  @Test
  public void testGetFunctionDefinitions() {
    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();

    final Collection<FunctionDefinition<?>> testFunctions =
        registry.getFunctionDefinitions(FunctionReturnType.TEXT);

    assertThat(testFunctions, notNullValue());
    assertThat(testFunctions.size(), is(NUM_TEXT_FUNCTIONS));

    final Collection<FunctionDefinition<?>> jsonFunctions =
        registry.getFunctionDefinitions(FunctionReturnType.JSON);

    assertThat(jsonFunctions, notNullValue());
    assertThat(jsonFunctions.size(), is(NUM_JSON_FUNCTIONS));
  }

  @Test
  public void testGetName() {
    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
    assertThat(registry.getName(), is("Function Definitions"));
  }

  @Test
  public void testGetRegisteredPlugins() {
    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
    final Collection<PropertyName> functionDefinitions = registry.getRegisteredPlugins();

    assertThat(functionDefinitions, notNullValue());

    final List<String> names =
        functionDefinitions.stream().map(PropertyName::getName).collect(toList());
    assertThat(names.size(), is(NUM_TEXT_FUNCTIONS + NUM_JSON_FUNCTIONS));
  }

  @Test
  public void testGetToolSpecifications() {
    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();

    final Collection<ToolSpecification> textToolSpecifications =
        registry.getToolSpecifications(FunctionReturnType.TEXT);
    assertThat(textToolSpecifications, notNullValue());
    assertThat(textToolSpecifications.size(), is(NUM_TEXT_FUNCTIONS));

    final Collection<ToolSpecification> jsonToolSpecifications =
        registry.getToolSpecifications(FunctionReturnType.JSON);
    assertThat(jsonToolSpecifications, notNullValue());
    assertThat(jsonToolSpecifications.size(), is(NUM_JSON_FUNCTIONS));
  }

  @Test
  public void testHasFunctionDefinition() {
    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();

    // Test with a function name that should exist
    // Note: This is a placeholder test. In a real scenario, you would need to know
    // the actual function names that should exist in the registry.
    final Collection<PropertyName> functionDefinitions = registry.getRegisteredPlugins();
    if (!functionDefinitions.isEmpty()) {
      final String functionName = functionDefinitions.iterator().next().getName();
      assertThat(registry.hasFunctionDefinition(functionName), is(true));
    }

    // Test with a function name that should not exist
    assertThat(registry.hasFunctionDefinition("non-existent-function"), is(false));
  }

  @Test
  public void testLookupFunctionDefinition() {
    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();

    // Test with a function name that should exist
    // Note: This is a placeholder test. In a real scenario, you would need to know
    // the actual function names that should exist in the registry.
    final Collection<PropertyName> functionDefinitions = registry.getRegisteredPlugins();
    if (!functionDefinitions.isEmpty()) {
      final String functionName = functionDefinitions.iterator().next().getName();
      final Optional<FunctionDefinition<?>> functionDefinition =
          registry.lookupFunctionDefinition(functionName);
      assertThat(functionDefinition.isPresent(), is(true));
      assertThat(functionDefinition.get().getName(), is(functionName));
    }

    // Test with a function name that should not exist
    final Optional<FunctionDefinition<?>> nonExistentFunction =
        registry.lookupFunctionDefinition("non-existent-function");
    assertThat(nonExistentFunction.isPresent(), is(false));
  }
}
