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

package schemacrawler.tools.command.aichat.options;

import static us.fatehi.utility.Utility.requireNotBlank;
import schemacrawler.tools.executable.CommandOptions;

public record AiChatCommandOptions(
    String aiProvider, String apiKey, String model, int timeout, int context, boolean useMetadata)
    implements CommandOptions {

  private static final int DEFAULT_CONTEXT = 10;
  private static final int MAXIMUM_CONTEXT = 50;
  private static final int DEFAULT_TIMEOUT = 10;
  private static final int MAXIMUM_TIMEOUT = 180;

  public AiChatCommandOptions {
    aiProvider = requireNotBlank(aiProvider, "No AI provider provided");
    apiKey = requireNotBlank(apiKey, "No OpenAI API key provided");
    model = requireNotBlank(model, "No AI model provided");

    if (!inIntegerRange(timeout, -1, MAXIMUM_TIMEOUT)) {
      timeout = DEFAULT_TIMEOUT;
    }

    if (!inIntegerRange(context, 0, MAXIMUM_CONTEXT)) {
      context = DEFAULT_CONTEXT;
    }
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder
        .append("AiChatCommandOptions [aiProvider=")
        .append(aiProvider)
        .append(", model=")
        .append(model)
        .append(", timeout=")
        .append(timeout)
        .append(", context=")
        .append(context)
        .append(", useMetadata=")
        .append(useMetadata)
        .append("]");
    return builder.toString();
  }

  private static boolean inIntegerRange(final int value, final int min, final int max) {
    return value > min && value <= max;
  }
}
