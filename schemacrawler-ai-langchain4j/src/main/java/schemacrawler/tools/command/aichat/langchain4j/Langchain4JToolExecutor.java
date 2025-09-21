/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.langchain4j;

import static java.util.Objects.requireNonNull;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.service.tool.ToolExecutor;
import java.sql.Connection;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.ai.tools.FunctionCallback;
import schemacrawler.tools.ai.tools.FunctionReturn;

public final class Langchain4JToolExecutor implements ToolExecutor {

  private final FunctionCallback functionToolExecutor;
  private final Connection connection;

  public Langchain4JToolExecutor(
      final String functionName, final Catalog catalog, final Connection connection) {
    functionToolExecutor = new FunctionCallback(functionName, catalog);
    this.connection = requireNonNull(connection, "No database connection provided");
  }

  @Override
  public String execute(final ToolExecutionRequest toolExecutionRequest, final Object memoryId) {
    requireNonNull(toolExecutionRequest, "No tool execution request provided");

    final String functionName = toolExecutionRequest.name();
    if (!functionToolExecutor.getFunctionName().getName().equals(functionName)) {
      throw new SchemaCrawlerException(String.format("Cannot execute function <>", functionName));
    }
    final String arguments = toolExecutionRequest.arguments();
    final FunctionReturn functionReturn = functionToolExecutor.execute(arguments, connection);
    return functionReturn.get();
  }

  @Override
  public String toString() {
    return functionToolExecutor.getFunctionName().toString();
  }
}
