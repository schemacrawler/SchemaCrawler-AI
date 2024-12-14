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

import java.io.PrintStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import static java.util.Objects.requireNonNull;
import io.github.sashirestela.openai.common.function.FunctionCall;
import io.github.sashirestela.openai.common.function.FunctionDef;
import io.github.sashirestela.openai.common.function.FunctionExecutor;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.AssistantMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.ChatRole;
import io.github.sashirestela.openai.domain.chat.ChatMessage.ToolMessage;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.aichat.FunctionDefinition;
import schemacrawler.tools.command.aichat.FunctionDefinition.FunctionType;
import schemacrawler.tools.command.aichat.FunctionParameters;
import schemacrawler.tools.command.aichat.FunctionReturn;
import schemacrawler.tools.command.aichat.functions.FunctionDefinitionRegistry;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
public class AiChatUtility {

  private static final Logger LOGGER = Logger.getLogger(AiChatUtility.class.getCanonicalName());

  public static <P extends FunctionParameters> String execute(
      final FunctionCall functionCall, final Catalog catalog, final Connection connection) {
    requireNonNull(functionCall, "No function call provided");

    // Look up function definition
    final FunctionDefinition<P> functionDefinitionToCall =
        (FunctionDefinition<P>) lookupFunctionDefinition(functionCall);
    if (functionDefinitionToCall == null) {
      return "";
    }

    // Build parameters
    final Class<P> parametersClass = functionDefinitionToCall.getParametersClass();
    final P parameters = instantiateArgs(functionCall, parametersClass);

    // Execute function
    FunctionReturn functionReturn;
    try {
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
              "Could not call function with arguments: %s(%s)",
              functionDefinitionToCall, functionCall.getArguments()));
      return "";
    }
  }

  public static boolean inIntegerRange(final int value, final int min, final int max) {
    return value > min && value <= max;
  }

  public static boolean isExitCondition(final List<ChatMessage> completions) {
    requireNonNull(completions, "No completions provided");
    for (final ChatMessage c : completions) {
      if (c.getRole() == ChatRole.TOOL && c.toString().contains("Thank you")) {
        return true;
      }
    }
    return false;
  }

  public static FunctionExecutor newFunctionExecutor() {

    final List<FunctionDef> chatFunctions = new ArrayList<>();
    for (final FunctionDefinition<?> functionDefinition :
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry().getFunctionDefinitions()) {
      if (functionDefinition.getFunctionType() != FunctionType.USER) {
        continue;
      }

      final FunctionDef chatFunction =
          FunctionDef.builder()
              .name(functionDefinition.getName())
              .description(functionDefinition.getDescription())
              .functionalClass(functionDefinition.getParametersClass())
              .strict(Boolean.FALSE)
              .build();
      chatFunctions.add(chatFunction);
    }
    return new FunctionExecutor(chatFunctions);
  }

  /**
   * Print AI chat API response.
   *
   * @param completions Chat completions
   * @param out Output stream to print to
   */
  public static void printResponse(final List<ChatMessage> completions, final PrintStream out) {
    requireNonNull(out, "No ouput stream provided");
    requireNonNull(completions, "No completions provided");
    for (final ChatMessage chatMessage : completions) {
      if (chatMessage instanceof final ToolMessage toolMessage) {
        out.println(toolMessage.getContent());
      }
      if (chatMessage instanceof final AssistantMessage assistantMessage) {
        out.println(assistantMessage.getContent());
      }
    }
  }

  private static <P extends FunctionParameters> P instantiateArgs(
      final FunctionCall functionCall, final Class<P> parametersClass) {
    final P parameters;
    final ObjectMapper objectMapper = new ObjectMapper();
    try {
      parameters = objectMapper.readValue(functionCall.getArguments(), parametersClass);
      System.out.println(parameters);
    } catch (final Exception e) {
      LOGGER.log(
          Level.INFO,
          e,
          new StringFormat(
              "Function parameters could not be instantiated: %s(%s)",
              parametersClass.getName(), functionCall.getArguments()));
      return null;
    }
    return parameters;
  }

  private static FunctionDefinition<?> lookupFunctionDefinition(final FunctionCall functionCall) {
    FunctionDefinition<?> functionDefinitionToCall = null;
    for (final FunctionDefinition<?> functionDefinition :
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry().getFunctionDefinitions()) {
      if (functionDefinition.getFunctionType() != FunctionType.USER) {
        continue;
      }
      if (functionDefinition.getName().equals(functionCall.getName())) {
        functionDefinitionToCall = functionDefinition;
        break;
      }
    }
    if (functionDefinitionToCall == null) {
      LOGGER.log(
          Level.INFO,
          new StringFormat(
              "Function not found: %s(%s)", functionCall.getName(), functionCall.getArguments()));
      return null;
    }
    return functionDefinitionToCall;
  }

  private AiChatUtility() {
    // Prevent instantiation
  }
}
