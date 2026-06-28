/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.tools;

import static java.util.Objects.requireNonNullElse;
import static us.fatehi.utility.Utility.trimToEmpty;

public record TextFunctionReturn(String text, FunctionReturnMetadata metadata)
    implements FunctionReturn {

  public TextFunctionReturn(final String text) {
    this(text, FunctionReturnMetadata.TEXT);
  }

  public TextFunctionReturn {
    text = trimToEmpty(text);
    metadata = requireNonNullElse(metadata, FunctionReturnMetadata.TEXT);
  }

  @Override
  public String get() {
    return text;
  }

  @Override
  public FunctionReturnMetadata getMetadata() {
    return metadata;
  }

  @Override
  public String getSummary() {
    return "";
  }
}
