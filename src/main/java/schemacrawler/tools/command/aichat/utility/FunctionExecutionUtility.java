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

package schemacrawler.tools.command.aichat.utility;

import java.sql.Connection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.aichat.FunctionDefinition;
import schemacrawler.tools.command.aichat.FunctionParameters;
import schemacrawler.tools.command.aichat.FunctionReturn;
import schemacrawler.tools.command.aichat.functions.FunctionDefinitionRegistry;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
public class FunctionExecutionUtility {

  private static final Logger LOGGER =
      Logger.getLogger(FunctionExecutionUtility.class.getCanonicalName());

  public static <P extends FunctionParameters> String execute(
      final String functionName,
      final String arguments,
      final Catalog catalog,
      final Connection connection) {
    requireNotBlank(functionName, "No function name provided");
    requireNotBlank(arguments, "No function arguments provided");

    try {
      // Look up function definition
      final Optional<FunctionDefinition<?>> lookedupFunctionDefinition =
          FunctionDefinitionRegistry.getFunctionDefinitionRegistry()
              .lookupFunctionDefinition(functionName);
      final FunctionDefinition<P> functionDefinitionToCall;
      if (lookedupFunctionDefinition.isEmpty()) {
        return "";
      }
      functionDefinitionToCall = (FunctionDefinition<P>) lookedupFunctionDefinition.get();

      // Build parameters
      final Class<P> parametersClass = functionDefinitionToCall.getParametersClass();
      final P parameters = instantiateParameters(arguments, parametersClass);

      // Execute function
      FunctionReturn functionReturn;
      final schemacrawler.tools.command.aichat.FunctionExecutor<P> functionExecutor =
          functionDefinitionToCall.newExecutor();
      functionExecutor.configure(parameters);
      functionExecutor.initialize();
      functionExecutor.setCatalog(catalog);
      if (functionExecutor.usesConnection()) {
        functionExecutor.setConnection(connection);
      }
      functionReturn = functionExecutor.call();
      return functionReturn.get();
    } catch (final Exception e) {
      LOGGER.log(
          Level.INFO,
          e,
          new StringFormat(
              "Could not call function with arguments: %s(%s)", functionName, arguments));
      return e.getMessage();
    }
  }

  public static <P extends FunctionParameters> P instantiateParameters(
      final String arguments, final Class<P> parametersClass) throws Exception {

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

  private FunctionExecutionUtility() {
    // Prevent instantiation
  }
}
