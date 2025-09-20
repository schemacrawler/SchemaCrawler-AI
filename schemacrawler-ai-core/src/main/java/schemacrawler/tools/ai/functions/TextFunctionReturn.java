/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import static us.fatehi.utility.Utility.trimToEmpty;

import schemacrawler.tools.ai.tools.FunctionReturn;

public class TextFunctionReturn implements FunctionReturn {

  private final String text;

  public TextFunctionReturn(final String text) {
    this.text = trimToEmpty(text);
  }

  @Override
  public String get() {
    return text;
  }
}
