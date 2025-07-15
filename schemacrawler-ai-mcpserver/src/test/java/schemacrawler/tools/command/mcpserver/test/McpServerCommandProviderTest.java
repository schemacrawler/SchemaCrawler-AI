/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.mcpserver.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.PluginCommandTestUtility;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.command.mcpserver.McpServerCommand;
import schemacrawler.tools.command.mcpserver.McpServerCommandOptions;
import schemacrawler.tools.command.mcpserver.McpServerCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;

@ResolveTestContext
public class McpServerCommandProviderTest {

  @Test
  public void mcpServerCommandProviderHelpCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new McpServerCommandProvider().getHelpCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void mcpServerCommandProviderPluginCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new McpServerCommandProvider().getCommandLineCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void newSchemaCrawlerCommand() {
    final McpServerCommandProvider commandProvider = new McpServerCommandProvider();
    assertThrows(
        IllegalArgumentException.class,
        () -> commandProvider.newSchemaCrawlerCommand("bad-command", new Config()));

    assertDoesNotThrow(() -> commandProvider.newSchemaCrawlerCommand("mcpserver", new Config()));

    final Config config = new Config();
    config.put("api-key", "api-key");
    final McpServerCommand command = commandProvider.newSchemaCrawlerCommand("mcpserver", config);
    final McpServerCommandOptions commandOptions = command.getCommandOptions();
    assertThat(commandOptions.mcpTransport().name(), is("stdio"));
  }

  @Test
  public void pluginCommand(final TestContext testContext) throws IOException {
    final McpServerCommandProvider commandProvider = new McpServerCommandProvider();
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.write(commandProvider.getCommandLineCommand().toString());
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void supportsOutputFormat() {
    final McpServerCommandProvider commandProvider = new McpServerCommandProvider();
    assertThat(commandProvider.supportsOutputFormat(null, null), is(true));
  }
}
