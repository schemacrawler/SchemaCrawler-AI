/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools;

import static us.fatehi.utility.Utility.trimToEmpty;

public final class TextFunctionReturn implements FunctionReturn {

  private final String text;

  public TextFunctionReturn(final String text) {
    this.text = trimToEmpty(text);
  }

  @Override
  public String get() {
    return text;
  }
}
