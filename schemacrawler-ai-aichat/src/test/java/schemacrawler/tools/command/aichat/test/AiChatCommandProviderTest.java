/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */


package schemacrawler.tools.command.aichat.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.test.utility.PluginCommandTestUtility;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.command.aichat.AiChatCommand;
import schemacrawler.tools.command.aichat.AiChatCommandProvider;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;

@ResolveTestContext
public class AiChatCommandProviderTest {

  @Test
  public void aiChatCommandProviderHelpCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new AiChatCommandProvider().getHelpCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void aiChatCommandProviderPluginCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new AiChatCommandProvider().getCommandLineCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void newSchemaCrawlerCommand() {
    final AiChatCommandProvider commandProvider = new AiChatCommandProvider();
    assertThrows(
        IllegalArgumentException.class,
        () -> commandProvider.newSchemaCrawlerCommand("bad-command", new Config()));

    assertThrows(
        ExecutionRuntimeException.class,
        () -> commandProvider.newSchemaCrawlerCommand("aichat", new Config()));

    final Config config = new Config();
    config.put("api-key", "api-key");
    final AiChatCommand command = commandProvider.newSchemaCrawlerCommand("aichat", config);
    final AiChatCommandOptions commandOptions = command.getCommandOptions();
    assertThat(commandOptions.apiKey(), is("api-key"));
    assertThat(commandOptions.model(), startsWith("gpt-4o-mini"));
  }

  @Test
  public void pluginCommand(final TestContext testContext) throws IOException {
    final AiChatCommandProvider commandProvider = new AiChatCommandProvider();
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.write(commandProvider.getCommandLineCommand().toString());
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void supportsOutputFormat() {
    final AiChatCommandProvider commandProvider = new AiChatCommandProvider();
    assertThat(commandProvider.supportsOutputFormat(null, null), is(true));
  }
}
