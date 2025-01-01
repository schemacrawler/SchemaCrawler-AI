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

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;
import schemacrawler.tools.command.aichat.utility.lanchain4j.Langchain4JChatAssistant;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import us.fatehi.utility.property.PropertyName;

/** SchemaCrawler command plug-in. */
public final class AiChatCommand extends BaseSchemaCrawlerCommand<AiChatCommandOptions> {

  private static final Logger LOGGER = Logger.getLogger(AiChatCommand.class.getName());

  static final PropertyName COMMAND =
      new PropertyName("aichat", "SchemaCrawler AI chat integration");

  protected AiChatCommand() {
    super(COMMAND);
  }

  @Override
  public void checkAvailability() throws RuntimeException {
    LOGGER.log(Level.FINE, "Looking for OPENAI_API_KEY environmental variable");
    final String apiKey = commandOptions.getApiKey();
    if (isBlank(apiKey)) {
      throw new SchemaCrawlerException("OPENAI_API_KEY not provided");
    }
  }

  @Override
  public void execute() {
    final String PROMPT = String.format("%nPrompt: ");
    try (final ChatAssistant chatAssistant =
            new Langchain4JChatAssistant(commandOptions, catalog, connection);
        // new SimpleOpenAIChatAssistant(commandOptions, catalog, connection);
        final Scanner scanner = new Scanner(System.in); ) {
      while (true) {
        System.out.print(PROMPT);
        final String prompt = scanner.nextLine();
        final String response = chatAssistant.chat(prompt);
        System.out.println(response);
        if (chatAssistant.shouldExit()) {
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
