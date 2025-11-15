/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.base.AbstractFunctionDefinition;

public final class TableSampleFunctionDefinition
    extends AbstractFunctionDefinition<TableSampleFunctionParameters> {

  @Override
  public String getDescription() {
    return """
    Profiles and samples table data by retrieving random rows for quick content
    analysis without full scans. Helps infer data patterns, validate types, and
    assess quality. Results vary per run (they are random picks) and offer
    insight for ETL, query design, and pipeline planning.
    Returns data as a JSON object.
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
  public String getTitle() {
    return "Sample table data";
  }

  @Override
  public TableSampleFunctionExecutor newExecutor() {
    return new TableSampleFunctionExecutor(getFunctionName());
  }
}
