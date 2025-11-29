/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools;

import static us.fatehi.utility.Utility.trimToEmpty;

public record TextFunctionReturn(String text) implements FunctionReturn {

  public TextFunctionReturn {
    text = trimToEmpty(text);
  }

  @Override
  public String get() {
    return text;
  }
}
