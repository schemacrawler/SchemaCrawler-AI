package schemacrawler.tools.command.aichat.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.test.utility.PluginCommandTestUtility;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
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
  public void pluginCommand() {
    final AiChatCommandProvider commandProvider = new AiChatCommandProvider();
    assertThat(
        commandProvider.getCommandLineCommand().toString(),
        is(
            "PluginCommand[name='aichat', options=["
                + "PluginCommandOption[name='api-key', valueClass=java.lang.String], "
                + "PluginCommandOption[name='api-key:env', valueClass=java.lang.String], "
                + "PluginCommandOption[name='model', valueClass=java.lang.String], "
                + "PluginCommandOption[name='timeout', valueClass=java.lang.Integer], "
                + "PluginCommandOption[name='context', valueClass=java.lang.Integer], "
                + "PluginCommandOption[name='use-metadata', valueClass=java.lang.Boolean]"
                + "]]"));
  }

  @Test
  public void supportsOutputFormat() {
    final AiChatCommandProvider commandProvider = new AiChatCommandProvider();
    assertThat(commandProvider.supportsOutputFormat(null, null), is(true));
  }
}
