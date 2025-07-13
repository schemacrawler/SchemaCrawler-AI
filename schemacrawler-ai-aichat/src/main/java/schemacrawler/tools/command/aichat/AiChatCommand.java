/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.ai.chat.ChatOptions;
import schemacrawler.tools.ai.chat.ChatAssistant;
import schemacrawler.tools.ai.chat.ChatAssistantRegistry;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import us.fatehi.utility.property.PropertyName;

/** SchemaCrawler command plug-in. */
public final class AiChatCommand extends BaseSchemaCrawlerCommand<ChatOptions> {

  private static final Logger LOGGER = Logger.getLogger(AiChatCommand.class.getName());

  static final PropertyName COMMAND =
      new PropertyName("aichat", "Chat with an AI agent that has access to your schema");

  protected AiChatCommand() {
    super(COMMAND);
  }

  @Override
  public void checkAvailability() throws RuntimeException {
    LOGGER.log(Level.FINE, "Looking for API key");
    final String apiKey = commandOptions.apiKey();
    if (isBlank(apiKey)) {
      throw new SchemaCrawlerException("API key not provided");
    }
  }

  @Override
  public void execute() {
    final String PROMPT = String.format("%nPrompt: ");

    // Load ChatAssistant implementation using ChatAssistantRegistry
    final ChatAssistantRegistry registry = ChatAssistantRegistry.getChatAssistantRegistry();
    final ChatAssistant chatAssistant =
        registry.newChatAssistant(commandOptions, catalog, connection);

    try (final ChatAssistant assistant = chatAssistant;
        final Scanner scanner = new Scanner(System.in)) {
      while (true) {
        System.out.print(PROMPT);
        final String prompt = scanner.nextLine();
        final String response = assistant.chat(prompt);
        System.out.println(response);
        if (assistant.shouldExit()) {
          return;
        }
      }
    } catch (final Exception e) {
      throw new SchemaCrawlerException(e);
    }
  }

  @Override
  public boolean usesConnection() {
    // Support commands that use connections
    return true;
  }
}
