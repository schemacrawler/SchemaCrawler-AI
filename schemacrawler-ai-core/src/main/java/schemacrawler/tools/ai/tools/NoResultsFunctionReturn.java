/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.tools;

public record NoResultsFunctionReturn() implements FunctionReturn {

  @Override
  public FunctionReturnMetadata getMetadata() {
    return FunctionReturnMetadata.TEXT;
  }

  @Override
  public String get() {
    return "No results returned from tool call.";
  }

  @Override
  public String getSummary() {
    return get();
  }
}
