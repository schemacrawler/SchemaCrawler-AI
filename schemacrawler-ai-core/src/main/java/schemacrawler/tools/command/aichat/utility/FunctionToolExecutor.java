package schemacrawler.tools.command.aichat.utility;

import java.sql.Connection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.aichat.tools.FunctionDefinition;
import schemacrawler.tools.command.aichat.tools.FunctionDefinitionRegistry;
import schemacrawler.tools.command.aichat.tools.FunctionExecutor;
import schemacrawler.tools.command.aichat.tools.FunctionParameters;
import schemacrawler.tools.command.aichat.tools.FunctionReturn;
import us.fatehi.utility.string.StringFormat;

public final class FunctionToolExecutor {

  private static final Logger LOGGER =
      Logger.getLogger(FunctionToolExecutor.class.getCanonicalName());

  private FunctionDefinition<FunctionParameters> functionDefinition;
  private final Catalog catalog;
  private final Connection connection;

  public FunctionToolExecutor(
      final String functionName, final Catalog catalog, final Connection connection) {
    this.catalog = catalog;
    this.connection = connection;

    // Look up function definition
    final Optional<FunctionDefinition<?>> lookedupFunctionDefinition =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry()
            .lookupFunctionDefinition(functionName);
    if (!lookedupFunctionDefinition.isEmpty()) {
      functionDefinition =
          (FunctionDefinition<FunctionParameters>) lookedupFunctionDefinition.get();
    } else {
      LOGGER.log(Level.WARNING, new StringFormat("Function <%s> not found", functionName));
      functionDefinition = null;
    }
  }

  public String execute(final String argumentsString) {
    final String functionName = getFunctionName();
    LOGGER.log(
        Level.INFO, new StringFormat("Executing <%s> with <%s> %n", functionName, argumentsString));

    if (functionDefinition == null) {
      return "";
    }

    try {
      final FunctionParameters arguments = instantiateArguments(argumentsString);

      final String returnValue = executeFunction(arguments);
      return returnValue;
    } catch (final Exception e) {
      LOGGER.log(
          Level.INFO,
          e,
          new StringFormat(
              "Could not call function with arguments: %s(%s)", functionName, argumentsString));
      return e.getMessage();
    }
  }

  public String getFunctionName() {
    if (functionDefinition == null) {
      return "unspecified-function";
    }
    return functionDefinition.getName();
  }

  @Override
  public String toString() {
    return getFunctionName();
  }

  private String executeFunction(final FunctionParameters arguments) {
    requireNonNull(arguments, "No function arguments provided");

    try {
      FunctionReturn functionReturn;
      final FunctionExecutor<FunctionParameters> functionExecutor =
          functionDefinition.newExecutor();
      functionExecutor.configure(arguments);
      functionExecutor.initialize();
      functionExecutor.setCatalog(catalog);
      if (functionExecutor.usesConnection()) {
        functionExecutor.setConnection(connection);
      }
      functionReturn = functionExecutor.call();
      final String returnValue = functionReturn.get();
      return returnValue;
    } catch (final Exception e) {
      LOGGER.log(
          Level.INFO,
          e,
          new StringFormat(
              "Could not call function with arguments: %s(%s)",
              functionDefinition.getName(), arguments));
      return e.getMessage();
    }
  }

  private <P extends FunctionParameters> P instantiateArguments(final String arguments)
      throws Exception {
    final Class<P> parametersClass = (Class<P>) functionDefinition.getParametersClass();
    final String functionArguments;
    if (isBlank(arguments)) {
      functionArguments = "{}";
    } else {
      functionArguments = arguments;
    }
    final ObjectMapper objectMapper = new ObjectMapper();
    try {
      final P parameters = objectMapper.readValue(functionArguments, parametersClass);
      LOGGER.log(Level.FINE, String.valueOf(parameters));
      return parameters;
    } catch (final Exception e) {
      LOGGER.log(
          Level.INFO,
          e,
          new StringFormat(
              "Function parameters could not be instantiated: %s(%s)",
              parametersClass.getName(), functionArguments));
      return parametersClass.getDeclaredConstructor().newInstance();
    }
  }
}
