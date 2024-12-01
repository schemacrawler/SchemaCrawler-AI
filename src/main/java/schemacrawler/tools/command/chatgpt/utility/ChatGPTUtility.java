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

package schemacrawler.tools.command.chatgpt.utility;

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
import schemacrawler.tools.command.chatgpt.FunctionDefinition;
import schemacrawler.tools.command.chatgpt.FunctionDefinition.FunctionType;
import schemacrawler.tools.command.chatgpt.FunctionParameters;
import schemacrawler.tools.command.chatgpt.FunctionReturn;
import schemacrawler.tools.command.chatgpt.functions.FunctionDefinitionRegistry;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
public class ChatGPTUtility {

  private static final Logger LOGGER = Logger.getLogger(ChatGPTUtility.class.getCanonicalName());

  public static <P extends FunctionParameters> String execute(
      final FunctionCall functionCall, final Catalog catalog, final Connection connection) {
    requireNonNull(functionCall, "No function call provided");

    FunctionDefinition<?> functionToCall = null;
    for (final FunctionDefinition<?> functionDefinition :
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry().getFunctionDefinitions()) {
      if (functionDefinition.getFunctionType() != FunctionType.USER) {
        continue;
      }
      if (functionDefinition.getName().equals(functionCall.getName())) {
        functionToCall = functionDefinition;
        break;
      }
    }
    if (functionToCall == null) {
      LOGGER.log(
          Level.INFO,
          new StringFormat(
              "Function not found: %s(%s)", functionCall.getName(), functionCall.getArguments()));
      return "";
    }

    // Build parameters
    final P parameters;
    final ObjectMapper objectMapper = new ObjectMapper();
    try {
      parameters =
          objectMapper.readValue(
              functionCall.getArguments(), (Class<P>) functionToCall.getParametersClass());
      System.out.println(parameters);
    } catch (final Exception e) {
      LOGGER.log(
          Level.INFO,
          e,
          new StringFormat(
              "Function parameters could not be instantiated: %s(%s)",
              functionToCall.getParametersClass().getName(), functionCall.getArguments()));
      return "";
    }
    // Execute function
    FunctionReturn functionReturn;
    try {
      final schemacrawler.tools.command.chatgpt.FunctionExecutor<P> functionExecutor =
          (schemacrawler.tools.command.chatgpt.FunctionExecutor<P>) functionToCall.newExecutor();
      functionExecutor.initialize(parameters, catalog, connection);
      functionReturn = functionExecutor.call();
    } catch (final Exception e) {
      LOGGER.log(
          Level.INFO,
          e,
          new StringFormat(
              "Could not call function with arguments: %s(%s)",
              functionToCall, functionCall.getArguments()));
      return "";
    }

    return functionReturn.get();
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
    for (final FunctionDefinition functionDefinition :
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
   * Send prompt to ChatGPT API and display response
   *
   * @param prompt Input prompt.
   */
  public static void printResponse(final List<ChatMessage> completions, final PrintStream out) {
    requireNonNull(out, "No ouput stream provided");
    requireNonNull(completions, "No completions provided");
    for (final ChatMessage chatMessage : completions) {
      if (chatMessage instanceof ToolMessage toolMessage) {
        out.println(toolMessage.getContent());
      }
      if (chatMessage instanceof AssistantMessage assistantMessage) {
        out.println(assistantMessage.getContent());
      }
    }
  }

  private ChatGPTUtility() {
    // Prevent instantiation
  }
}
