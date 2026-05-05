/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.tools;

import static us.fatehi.utility.Utility.trimToEmpty;

public record TextFunctionReturn(String text, String format, String mediaType)
    implements FunctionReturn {

  public TextFunctionReturn(final String text) {
    this(text, "text", "text/plain");
  }

  public TextFunctionReturn {
    text = trimToEmpty(text);
    format = trimToEmpty(format);
    mediaType = trimToEmpty(mediaType);
  }

  @Override
  public String get() {
    return text;
  }
}
