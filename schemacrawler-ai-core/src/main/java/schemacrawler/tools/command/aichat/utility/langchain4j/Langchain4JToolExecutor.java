package schemacrawler.tools.command.aichat.utility.langchain4j;

import java.sql.Connection;
import static java.util.Objects.requireNonNull;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.service.tool.ToolExecutor;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.command.aichat.utility.FunctionToolExecutor;

public final class Langchain4JToolExecutor implements ToolExecutor {

  private final FunctionToolExecutor functionToolExecutor;

  public Langchain4JToolExecutor(
      final String functionName, final Catalog catalog, final Connection connection) {
    functionToolExecutor = new FunctionToolExecutor(functionName, catalog, connection);
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
