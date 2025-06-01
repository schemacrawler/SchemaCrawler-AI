/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
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
import schemacrawler.tools.command.aichat.functions.json.DescribeRoutinesFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.json.DescribeTablesFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.json.ListAcrossTablesFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.json.ListFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.text.DatabaseObjectDescriptionFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.text.DatabaseObjectListFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.text.ExitFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.text.LintFunctionDefinition;
import us.fatehi.utility.property.PropertyName;

public class FunctionDefinitionRegistryTest {

  private static final int NUM_FUNCTIONS = 8;
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

    assertThat(functionDefinitions, hasSize(NUM_FUNCTIONS));

    final List<String> names =
        functionDefinitions.stream().map(PropertyName::getName).collect(toList());
    assertThat(
        names,
        containsInAnyOrder(
            "database-object-list",
            "database-object-description",
            "lint",
            "exit",
            "describe-tables",
            "describe-routines",
            "list",
            "list-across-tables"));
  }

  @Test
  public void testCommandPlugin() throws Exception {
    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
    final Collection<FunctionDefinition<?>> functions = registry.getFunctionDefinitions();
    assertThat(functions, hasSize(NUM_FUNCTIONS));
    assertThat(
        functions.stream()
            .map(function -> function.getClass().getSimpleName())
            .collect(Collectors.toList()),
        containsInAnyOrder(
            DatabaseObjectListFunctionDefinition.class.getSimpleName(),
            DescribeTablesFunctionDefinition.class.getSimpleName(),
            DescribeRoutinesFunctionDefinition.class.getSimpleName(),
            DatabaseObjectDescriptionFunctionDefinition.class.getSimpleName(),
            LintFunctionDefinition.class.getSimpleName(),
            ExitFunctionDefinition.class.getSimpleName(),
            ListFunctionDefinition.class.getSimpleName(),
            ListAcrossTablesFunctionDefinition.class.getSimpleName()));
  }

  @Test
  public void testGetFunctionDefinitions() {
    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
    final Collection<FunctionDefinition<?>> functions = registry.getFunctionDefinitions();

    assertThat(functions, notNullValue());
    assertThat(functions.size(), is(NUM_FUNCTIONS));
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
    assertThat(names.size(), is(NUM_FUNCTIONS));
  }

  @Test
  public void testGetToolSpecifications() {
    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();

    final Collection<ToolSpecification> textToolSpecifications =
        registry.getToolSpecifications(FunctionReturnType.TEXT);
    assertThat(textToolSpecifications, notNullValue());
    assertThat(textToolSpecifications.size(), is(4));

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
