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

package schemacrawler.tools.command.aichat.test;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.command.aichat.FunctionDefinition;
import schemacrawler.tools.command.aichat.functions.DatabaseObjectDescriptionFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.DatabaseObjectListFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.ExitFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.FunctionDefinitionRegistry;
import schemacrawler.tools.command.aichat.functions.LintFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.TableDecriptionFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.TableReferencesFunctionDefinition;
import us.fatehi.utility.property.PropertyName;

public class FunctionDefinitionRegistryTest {

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

    assertThat(functionDefinitions, hasSize(6));

    final List<String> names =
        functionDefinitions.stream().map(PropertyName::getName).collect(toList());
    assertThat(
        names,
        containsInAnyOrder(
            "table-decription",
            "database-object-list",
            "table-references",
            "exit",
            "database-object-description",
            "lint"));
  }

  @Test
  public void testCommandPlugin() throws Exception {
    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
    final Collection<FunctionDefinition<?>> functions = registry.getFunctionDefinitions();
    assertThat(functions, hasSize(6));
    assertThat(
        functions.stream()
            .map(function -> function.getClass().getSimpleName())
            .collect(Collectors.toList()),
        containsInAnyOrder(
            DatabaseObjectListFunctionDefinition.class.getSimpleName(),
            TableDecriptionFunctionDefinition.class.getSimpleName(),
            TableReferencesFunctionDefinition.class.getSimpleName(),
            DatabaseObjectDescriptionFunctionDefinition.class.getSimpleName(),
            LintFunctionDefinition.class.getSimpleName(),
            ExitFunctionDefinition.class.getSimpleName()));
  }
}
