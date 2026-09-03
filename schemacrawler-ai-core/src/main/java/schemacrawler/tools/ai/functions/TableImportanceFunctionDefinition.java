/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.base.AbstractFunctionDefinition;

public final class TableImportanceFunctionDefinition
    extends AbstractFunctionDefinition<TableImportanceFunctionParameters> {

  @Override
  public String getDescription() {
    return """
    Returns schema graph importance metrics for tables and views, including dependency
    centrality, table counts, and traits. An optional regular expression filters fully
    qualified table and view names. Returns data as a JSON object.
    """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<TableImportanceFunctionParameters> getParametersClass() {
    return TableImportanceFunctionParameters.class;
  }

  @Override
  public String getTitle() {
    return "Report table and view importance";
  }

  @Override
  public TableImportanceFunctionExecutor newExecutor() {
    return new TableImportanceFunctionExecutor(getFunctionName());
  }

  @Override
  public TableImportanceFunctionParameters newParameters() {
    return new TableImportanceFunctionParameters();
  }
}
