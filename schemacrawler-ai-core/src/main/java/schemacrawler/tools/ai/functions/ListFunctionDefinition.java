/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.base.AbstractFunctionDefinition;

public final class ListFunctionDefinition
    extends AbstractFunctionDefinition<ListFunctionParameters> {

  @Override
  public String getDescription() {
    return """
    Provides a comprehensive database object inventory offering organized listings of all
    schema objects such as tables, views, stored procedures, functions, sequences, synonyms,
    and more. This is an essential starting point for database exploration, database asset
    management, and schema analysis. The tool supports object type filtering and pattern-based
    searching to streamline navigation across large databases.
    Returns JSON data.
    """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<ListFunctionParameters> getParametersClass() {
    return ListFunctionParameters.class;
  }

  @Override
  public String getTitle() {
    return "List database objects";
  }

  @Override
  public ListFunctionExecutor newExecutor() {
    return new ListFunctionExecutor(getFunctionName());
  }
}
