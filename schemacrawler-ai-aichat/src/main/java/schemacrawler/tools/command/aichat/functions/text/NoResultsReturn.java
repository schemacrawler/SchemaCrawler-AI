/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */



package schemacrawler.tools.command.aichat.functions.text;

import schemacrawler.tools.ai.tools.FunctionReturn;

public class NoResultsReturn implements FunctionReturn {

  @Override
  public String get() {
    return "There were no matching results for your query.";
  }
}
