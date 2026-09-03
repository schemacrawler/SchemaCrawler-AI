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
    Finds the shortest forward dependency path from one table or view to another.
    Both regular expressions must resolve to exactly one fully qualified table or view name.
    Foreign-key relationships are preferred; implied associations are used only as a fallback.
    Returns data as a JSON object.
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
    return "Find a forward table dependency path";
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
