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

package schemacrawler.tools.command.aichat.langchain4j;

import java.sql.Connection;
import static java.util.Objects.requireNonNull;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.service.tool.ToolExecutor;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.command.aichat.tools.FunctionCallback;

public final class Langchain4JToolExecutor implements ToolExecutor {

  private final FunctionCallback functionToolExecutor;

  public Langchain4JToolExecutor(
      final String functionName, final Catalog catalog, final Connection connection) {
    functionToolExecutor = new FunctionCallback(functionName, catalog, connection);
  }

  @Override
  public String execute(final ToolExecutionRequest toolExecutionRequest, final Object memoryId) {
    requireNonNull(toolExecutionRequest, "No tool execution request provided");

    final String functionName = toolExecutionRequest.name();
    if (!functionToolExecutor.getFunctionName().equals(functionName)) {
      throw new SchemaCrawlerException(String.format("Cannot execute function <>", functionName));
    }
    final String arguments = toolExecutionRequest.arguments();
    return functionToolExecutor.execute(arguments);
  }
}
