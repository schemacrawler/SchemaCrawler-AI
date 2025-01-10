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

package schemacrawler.tools.command.aichat;

import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;
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
            "One of openai, github, azure-openai",
            "Optional, defaults to openai")
        .addOption("api-key", String.class, "OpenAI API key")
        .addOption(
            "api-key:env", String.class, "OpenAI API key, from an environmental variable value")
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
      final AiChatCommandOptions options =
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
