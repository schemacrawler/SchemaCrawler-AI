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
    Provides data profiling and sampling for table content analysis, retrieving random sample
    rows to support data discovery, content validation, and data quality assessment without
    requiring full table scans. These rows are selected at random, so different rows may be
    returned each time they are requested. This should not be used as a substitute for running
    a query on a table, but rather as a way to infer what the table may contain. This helps
    with understanding data patterns, validating data types, identifying data quality issues,
    and supporting ETL development. Designed for data analysts, developers, and data engineers,
    it enables quick insight into table contents before writing queries or designing pipelines.
    Results return as actual data samples, offering a practical foundation for informed
    decision-making.
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
