/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.base.AbstractFunctionDefinition;

public final class ListAcrossTablesFunctionDefinition
    extends AbstractFunctionDefinition<ListAcrossTablesFunctionParameters> {

  @Override
  public String getDescription() {
    return """
    Makes an inventory of table-dependent objects across the database schema,
    including columns, indexes, foreign keys, triggers, and constraints. Enables
    impact analysis, dependency tracking, performance tuning, and refactoring
    without inspecting individual tables. The corresponding tables are identified,
    and their details can be obtained later by describing those tables. Supports
    regex-based table name filtering and configurable detail levels to optimize
    tool performance.
    Returns JSON data.
    """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<ListAcrossTablesFunctionParameters> getParametersClass() {
    return ListAcrossTablesFunctionParameters.class;
  }

  @Override
  public String getTitle() {
    return "List dependent database objects across tables";
  }

  @Override
  public ListAcrossTablesFunctionExecutor newExecutor() {
    return new ListAcrossTablesFunctionExecutor(getFunctionName());
  }
}
