package schemacrawler.tools.command.aichat.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.executable.CommandRegistry;
import us.fatehi.utility.property.PropertyName;

public class CommandPluginTest {

  @Test
  public void testCommandPlugin() throws Exception {
    final CommandRegistry registry = CommandRegistry.getCommandRegistry();
    assertThat(isCommandSupported(registry, "aichat"), is(true));
  }

  private boolean isCommandSupported(final CommandRegistry registry, final String command) {
    final Collection<PropertyName> supportedCommands = registry.getRegisteredPlugins();
    for (final PropertyName supportedCommand : supportedCommands) {
      if (supportedCommand.getName().equals(command)) {
        return true;
      }
    }
    return false;
  }
}
