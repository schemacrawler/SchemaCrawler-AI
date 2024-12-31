package us.fatehi.example;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.service.tool.ToolExecutor;
import schemacrawler.tools.command.aichat.utility.FunctionExecutionUtility;

public final class Langchain4JToolExecutor implements ToolExecutor {
  @Override
  public String execute(final ToolExecutionRequest toolExecutionRequest, final Object memoryId) {
    final String functionName = toolExecutionRequest.name();
    final String arguments = toolExecutionRequest.arguments();
    System.out.printf(
        "id=%s, name=%s, args=%s%n", toolExecutionRequest.id(), functionName, arguments);
    return FunctionExecutionUtility.execute(functionName, arguments, null, null);
  }
}
