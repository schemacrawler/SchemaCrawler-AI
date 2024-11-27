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
import org.apache.commons.math3.stat.descriptive.summary.Product;
import static java.util.Objects.requireNonNull;
import io.github.sashirestela.openai.common.function.FunctionDef;
import io.github.sashirestela.openai.common.function.FunctionExecutor;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.chatgpt.FunctionDefinition;
import schemacrawler.tools.command.chatgpt.FunctionDefinition.FunctionType;
import schemacrawler.tools.command.chatgpt.FunctionParameters;
import schemacrawler.tools.command.chatgpt.functions.ExitFunctionDefinition;
import schemacrawler.tools.command.chatgpt.functions.FunctionDefinitionRegistry;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class ChatGPTUtility {

  public static boolean inIntegerRange(final int value, final int min, final int max) {
    return value > min && value <= max;
  }

  public static boolean isExitCondition(final List<ChatMessage> completions) {
    requireNonNull(completions, "No completions provided");
    final String exitFunctionName = new ExitFunctionDefinition().getName();
    for (final ChatMessage c : completions) {
      if (c.getFunctionCall() != null && c.getName().equals(exitFunctionName)) {
        return true;
      }
    }
    return false;
  }

  public static FunctionExecutor newFunctionExecutor(
      final Catalog catalog, final Connection connection) {

    requireNonNull(catalog, "No catalog provided");
    requireNonNull(connection, "No connection provided");

    final List<FunctionDef> chatFunctions = new ArrayList<>();
    for (final FunctionDefinition<? extends FunctionParameters> functionDefinition :
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry().getFunctionDefinitions()) {
      if (functionDefinition.getFunctionType() != FunctionType.USER) {
        continue;
      }
      functionDefinition.setCatalog(catalog);
      functionDefinition.setConnection(connection);

      final FunctionDef chatFunction = FunctionDef.builder()
              .name(functionDefinition.getName())
              .description(functionDefinition.getDescription())
              .functionalClass(Product.class)
              //.executor(functionDefinition.getParameters(), functionDefinition.getExecutor())
              .strict(Boolean.TRUE)
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
      out.println(chatMessage.getContent());
    }
  }

  private ChatGPTUtility() {
    // Prevent instantiation
  }
}
