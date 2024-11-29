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

package schemacrawler.tools.command.chatgpt;

import static schemacrawler.tools.command.chatgpt.utility.ChatGPTUtility.isExitCondition;
import static schemacrawler.tools.command.chatgpt.utility.ChatGPTUtility.printResponse;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.common.function.FunctionCall;
import io.github.sashirestela.openai.common.function.FunctionExecutor;
import io.github.sashirestela.openai.common.tool.ToolCall;
import io.github.sashirestela.openai.domain.chat.Chat;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.ResponseMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.ToolMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.UserMessage;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.chatgpt.embeddings.QueryService;
import schemacrawler.tools.command.chatgpt.options.ChatGPTCommandOptions;
import schemacrawler.tools.command.chatgpt.utility.ChatGPTUtility;
import schemacrawler.tools.command.chatgpt.utility.ChatHistory;
import us.fatehi.utility.string.StringFormat;

public final class ChatGPTConsole implements AutoCloseable {

  private static final Logger LOGGER = Logger.getLogger(ChatGPTConsole.class.getCanonicalName());

  private static final String PROMPT = String.format("%nPrompt: ");

  private final ChatGPTCommandOptions commandOptions;
  private final FunctionExecutor functionExecutor;
  private final SimpleOpenAI service;
  private final QueryService queryService;
  private final ChatHistory chatHistory;
  private final boolean useMetadata;

  public ChatGPTConsole(
      final ChatGPTCommandOptions commandOptions,
      final Catalog catalog,
      final Connection connection) {

    this.commandOptions = requireNonNull(commandOptions, "ChatGPT options not provided");
    requireNonNull(catalog, "No catalog provided");
    requireNonNull(connection, "No connection provided");

    functionExecutor = ChatGPTUtility.newFunctionExecutor(catalog, connection);

    service = SimpleOpenAI.builder().apiKey(System.getenv("OPENAI_API_KEY")).build();

    queryService = new QueryService(service);
    queryService.addTables(catalog.getTables());

    useMetadata = commandOptions.isUseMetadata();
    chatHistory = new ChatHistory(commandOptions.getContext(), new ArrayList<>());
  }

  @Override
  public void close() {}

  /** Simple REPL for the SchemaCrawler ChatGPT integration. */
  public void console() {
    try (final Scanner scanner = new Scanner(System.in)) {
      while (true) {
        System.out.print(PROMPT);
        final String prompt = scanner.nextLine();
        final List<ChatMessage> completions = complete(prompt);
        printResponse(completions, System.out);
        if (isExitCondition(completions)) {
          return;
        }
      }
    }
  }

  /**
   * Send prompt to ChatGPT API and get completions.
   *
   * @param prompt Input prompt.
   */
  private List<ChatMessage> complete(final String prompt) {

    final List<ChatMessage> completions = new ArrayList<>();

    try {

      chatHistory.add(UserMessage.of(prompt));

      final List<ChatMessage> messages = chatHistory.toList();

      if (useMetadata) {
        final Collection<ChatMessage> chatMessages = queryService.query(prompt);
        messages.addAll(chatMessages);
      }

      final ChatRequest chatRequest =
          ChatRequest.builder()
              .model(commandOptions.getModel())
              .messages(messages)
              .tools(functionExecutor.getToolFunctions())
              .temperature(0.0)
              .build();
      final CompletableFuture<Chat> futureChat = service.chatCompletions().create(chatRequest);
      final Chat chatResponse = futureChat.join();

      System.out.println(String.format("Token usage: %s", chatResponse.getUsage()));
      LOGGER.log(Level.INFO, new StringFormat("Token usage: %s", chatResponse.getUsage()));
      // Assume only one message was returned, since we asked for only one
      final ResponseMessage responseMessage = chatResponse.firstMessage();

      final List<ToolCall> toolCalls = responseMessage.getToolCalls();
      if (toolCalls != null && !toolCalls.isEmpty()) {
        final ToolCall toolCall = toolCalls.get(0);
        final FunctionCall function = toolCall.getFunction();
        System.out.println(String.format("Function call: %s(%s)", function.getName(), function.getArguments()));
        LOGGER.log(Level.INFO, new StringFormat("Function call: %s(%s)", function.getName(), function.getArguments()));
        final FunctionReturn functionReturn = functionExecutor.execute(function);
        System.out.println(functionReturn.get());
        completions.add(ToolMessage.of(functionReturn.get(), toolCall.getId()));
      } else {
        System.out.println(chatResponse.firstContent());
        completions.add(responseMessage);
      }
    } catch (final Exception e) {
      e.printStackTrace();
      LOGGER.log(Level.INFO, e.getMessage(), e);
    }

    return completions;
  }
}
