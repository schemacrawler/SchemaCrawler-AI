/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.base.AbstractFunctionDefinition;

public final class TablePathFunctionDefinition
    extends AbstractFunctionDefinition<TablePathFunctionParameters> {

  @Override
  public String getDescription() {
    return """
    Finds the shortest forward dependency path from a source table or view to a target table
    or view in the schema graph.
    Requires source table name and target table name, each specified as a regular expression
    matching a fully qualified table or view name (each must resolve to exactly one match).
    Foreign-key relationships are preferred; implied associations (from column name matching)
    are used as a fallback. Searches are limited to 5 relationship hops by default; use
    `max_path_depth` to select another limit. A -1 value allows an unlimited search.
    Returns an ordered array of fully qualified table or view names representing the
    step-by-step dependency chain from source to target and a boolean flag indicating whether
    implied associations (implicit relationships) were required to complete the path.
    Returns a JSON object.
    """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<TablePathFunctionParameters> getParametersClass() {
    return TablePathFunctionParameters.class;
  }

  @Override
  public String getTitle() {
    return "Find table dependency path";
  }

  @Override
  public TablePathFunctionExecutor newExecutor() {
    return new TablePathFunctionExecutor(getFunctionName());
  }

  @Override
  public TablePathFunctionParameters newParameters() {
    return new TablePathFunctionParameters();
  }
}
