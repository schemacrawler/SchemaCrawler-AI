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

package schemacrawler.tools.command.simpleopenai.utility;

import java.io.PrintStream;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.common.function.FunctionDef;
import io.github.sashirestela.openai.common.function.FunctionExecutor;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.AssistantMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.ChatRole;
import io.github.sashirestela.openai.domain.chat.ChatMessage.ToolMessage;
import schemacrawler.tools.command.aichat.FunctionDefinition;
import schemacrawler.tools.command.aichat.FunctionDefinition.FunctionType;
import schemacrawler.tools.command.aichat.functions.FunctionDefinitionRegistry;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class SimpleOpenAIUtility {

  private static final Logger LOGGER =
      Logger.getLogger(SimpleOpenAIUtility.class.getCanonicalName());

  public static boolean isExitCondition(final List<ChatMessage> completions) {
    requireNonNull(completions, "No completions provided");
    for (final ChatMessage c : completions) {
      if (c.getRole() == ChatRole.TOOL && c.toString().contains("Thank you")) {
        return true;
      }
    }
    return false;
  }

  public static SimpleOpenAI newService(final AiChatCommandOptions commandOptions) {
    final HttpClient httpClient =
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(commandOptions.getTimeout()))
            .build();
    final SimpleOpenAI service =
        SimpleOpenAI.builder().apiKey(commandOptions.getApiKey()).httpClient(httpClient).build();

    return service;
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

  public static FunctionExecutor toolsList() {

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

  private SimpleOpenAIUtility() {
    // Prevent instantiation
  }
}
