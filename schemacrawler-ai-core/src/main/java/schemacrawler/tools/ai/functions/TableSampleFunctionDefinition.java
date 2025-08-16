/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

public final class TableSampleFunctionDefinition
    extends AbstractJsonFunctionDefinition<TableSampleFunctionParameters> {

  @Override
  public String getDescription() {
    return """
        Gets a few sample rows of data from a table.
        """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<TableSampleFunctionParameters> getParametersClass() {
    return TableSampleFunctionParameters.class;
  }

  @Override
  public TableSampleFunctionExecutor newExecutor() {
    return new TableSampleFunctionExecutor(getFunctionName(), getFunctionReturnType());
  }
}
