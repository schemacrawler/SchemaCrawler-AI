/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.langchain4j.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.mock;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.service.tool.ToolExecutor;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.tools.FunctionDefinitionRegistry;
import schemacrawler.tools.ai.tools.FunctionReturnType;
import schemacrawler.tools.command.aichat.langchain4j.Langchain4JUtility;

public class Langchain4JUtilityTest {

  @BeforeAll
  public static void setupClass() {
    // Ensure the FunctionDefinitionRegistry is initialized
    FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
  }

  @Test
  public void testToolExecutors() {
    // Arrange
    final Catalog catalog = mock(Catalog.class);
    final Connection connection = mock(Connection.class);

    // Act
    final Map<String, ToolExecutor> toolExecutors =
        Langchain4JUtility.toolExecutors(catalog, connection);

    // Assert
    assertThat(toolExecutors, is(notNullValue()));
    assertThat(toolExecutors.entrySet(), is(not(empty())));

    // Verify that the number of tool executors matches the number of user functions in the registry
    final int userFunctionCount =
        (int)
            FunctionDefinitionRegistry.getFunctionDefinitionRegistry()
                .getFunctionDefinitions(FunctionReturnType.TEXT)
                .stream()
                .count();

    assertThat(toolExecutors.size(), is(userFunctionCount));
  }

  @Test
  public void testTools() {
    // Act
    final List<ToolSpecification> tools = Langchain4JUtility.tools();

    // Assert
    assertThat(tools, is(notNullValue()));
    assertThat(tools, is(not(empty())));

    // Verify that each tool has the required properties
    for (final ToolSpecification tool : tools) {
      assertThat(tool.name(), is(notNullValue()));
      assertThat(tool.description(), is(notNullValue()));
      assertThat(tool.parameters(), is(notNullValue()));
    }
  }
}
