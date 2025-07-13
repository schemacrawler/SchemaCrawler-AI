/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.chat;

import static us.fatehi.utility.Utility.requireNotBlank;
import schemacrawler.tools.executable.CommandOptions;

public record ChatOptions(
    String aiProvider, String apiKey, String model, int timeout, int context, boolean useMetadata)
    implements CommandOptions {

  private static final int DEFAULT_CONTEXT = 10;
  private static final int MAXIMUM_CONTEXT = 50;
  private static final int DEFAULT_TIMEOUT = 10;
  private static final int MAXIMUM_TIMEOUT = 180;

  public ChatOptions {
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

  // NOTE: Do not expose the API key
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
