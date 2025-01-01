package schemacrawler.tools.command.utility.lanchain4j;

import java.sql.Connection;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.service.tool.ToolExecutor;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.aichat.utility.FunctionExecutionUtility;

public final class Langchain4JToolExecutor implements ToolExecutor {

  private final Catalog catalog;
  private final Connection connection;

  public Langchain4JToolExecutor(final Catalog catalog, final Connection connection) {
    this.catalog = catalog;
    this.connection = connection;
  }

  @Override
  public String execute(final ToolExecutionRequest toolExecutionRequest, final Object memoryId) {
    final String functionName = toolExecutionRequest.name();
    final String arguments = toolExecutionRequest.arguments();
    System.out.printf(
        "id=%s, name=%s, args=%s%n", toolExecutionRequest.id(), functionName, arguments);
    return FunctionExecutionUtility.execute(functionName, arguments, catalog, connection);
  }
}
