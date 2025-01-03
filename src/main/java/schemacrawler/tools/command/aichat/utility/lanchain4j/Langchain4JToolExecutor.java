package schemacrawler.tools.command.aichat.utility.lanchain4j;

import java.sql.Connection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import static us.fatehi.utility.Utility.requireNotBlank;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.service.tool.ToolExecutor;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.aichat.FunctionDefinition;
import schemacrawler.tools.command.aichat.FunctionParameters;
import schemacrawler.tools.command.aichat.functions.FunctionDefinitionRegistry;
import schemacrawler.tools.command.aichat.utility.FunctionExecutionUtility;
import us.fatehi.utility.string.StringFormat;

public final class Langchain4JToolExecutor implements ToolExecutor {

  private static final Logger LOGGER =
      Logger.getLogger(Langchain4JToolExecutor.class.getCanonicalName());

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
    LOGGER.log(
        Level.INFO,
        new StringFormat(String.format("Executing <%s> with <%s> %n", functionName, arguments)));
    System.err.print(String.format("Executing <%s> with <%s> %n", functionName, arguments));

    requireNotBlank(functionName, "No function name provided");
    requireNotBlank(arguments, "No function arguments provided");

    try {
      // Look up function definition
      final Optional<FunctionDefinition<?>> lookedupFunctionDefinition =
          FunctionDefinitionRegistry.getFunctionDefinitionRegistry()
              .lookupFunctionDefinition(functionName);
      final FunctionDefinition<FunctionParameters> functionDefinition;
      if (lookedupFunctionDefinition.isEmpty()) {
        return "";
      }
      functionDefinition =
          (FunctionDefinition<FunctionParameters>) lookedupFunctionDefinition.get();

      // Build parameters
      final Class<FunctionParameters> parametersClass = functionDefinition.getParametersClass();
      final FunctionParameters parameters =
          FunctionExecutionUtility.instantiateArguments(arguments, parametersClass);

      final String returnValue =
          FunctionExecutionUtility.executeFunction(
              functionDefinition, parameters, catalog, connection);
      return returnValue;
    } catch (final Exception e) {
      LOGGER.log(
          Level.INFO,
          e,
          new StringFormat(
              "Could not call function with arguments: %s(%s)", functionName, arguments));
      return e.getMessage();
    }
  }
}
