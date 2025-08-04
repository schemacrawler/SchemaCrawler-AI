/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat;

import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;

import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.ai.chat.ChatOptions;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptionsBuilder;
import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;

/** SchemaCrawler command plug-in for AI chat. */
public final class AiChatCommandProvider extends BaseCommandProvider {

  public AiChatCommandProvider() {
    super(AiChatCommand.COMMAND);
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    final PluginCommand pluginCommand = newPluginCommand(AiChatCommand.COMMAND);
    pluginCommand
        .addOption(
            "ai-provider",
            String.class,
            "AI provider",
            "One of openai, anthropic, github-models",
            "Optional, defaults to openai")
        .addOption("api-key", String.class, "AI provider's API key")
        .addOption(
            "api-key:env",
            String.class,
            "Environmental variable name, that has the AI provider's API key")
        .addOption("model", String.class, "AI chat model", "Optional, defaults to 'gpt-4o-mini'")
        .addOption(
            "timeout",
            Integer.class,
            "Number of seconds to timeout a request if no response is received",
            "Optional, defaults to 60")
        .addOption(
            "context",
            Integer.class,
            "Number of chat messages (not tokens) to maintain as chat context",
            "Optional, defaults to 10")
        .addOption(
            "use-metadata",
            Boolean.class,
            "Allow sharing of database metadata with AI model to enhance chat responses",
            "This is useful if you would like help with SQL queries",
            "Optional, defaults to false");
    return pluginCommand;
  }

  @Override
  public AiChatCommand newSchemaCrawlerCommand(final String command, final Config config) {
    if (!supportsCommand(command)) {
      throw new IllegalArgumentException("Cannot support command, " + command);
    }

    try {
      final ChatOptions options =
          AiChatCommandOptionsBuilder.builder().fromConfig(config).toOptions();

      final AiChatCommand scCommand = new AiChatCommand();
      scCommand.configure(options);
      return scCommand;
    } catch (final Exception e) {
      throw new ExecutionRuntimeException(e);
    }
  }

  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    return true;
  }
}
