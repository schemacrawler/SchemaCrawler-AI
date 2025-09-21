/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.langchain4j.test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.sql.Connection;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.command.aichat.langchain4j.Langchain4JToolExecutor;

public class Langchain4JToolExecutorTest {

  @Test
  public void testExecuteWithNullRequest() {
    // Arrange
    final FunctionDefinition<?> functionDefinition = mock(FunctionDefinition.class);

    final Catalog catalog = mock(Catalog.class);
    final Connection connection = mock(Connection.class);

    // Create the tool executor
    final Langchain4JToolExecutor toolExecutor =
        new Langchain4JToolExecutor(functionDefinition, catalog, connection);

    // Act & Assert
    assertThrows(NullPointerException.class, () -> toolExecutor.execute(null, null));
  }
}
