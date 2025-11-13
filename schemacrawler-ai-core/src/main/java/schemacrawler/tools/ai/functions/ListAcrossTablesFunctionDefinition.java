/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.base.AbstractFunctionDefinition;

public final class ListAcrossTablesFunctionDefinition
    extends AbstractFunctionDefinition<ListAcrossTablesFunctionParameters> {

  @Override
  public String getDescription() {
    return """
    Discovers and catalogs table-dependent objects across the entire database schema, including
    columns, indexes, foreign key relationships, triggers, and constraints. This is an
    essential starting point for impact analysis, dependency tracking, performance
    optimization, and database refactoring, enabling comprehensive relationship discovery
    without the need to inspect individual tables. It is particularly effective for identifying
    naming patterns, locating orphaned objects, and understanding cross-schema dependencies.
    The corresponding tables are identified, and their details can be obtained later by
    describing those tables. Tool parameters support filtering table names by regular
    expressions and selective detail scopes to optimize tool performance.
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
