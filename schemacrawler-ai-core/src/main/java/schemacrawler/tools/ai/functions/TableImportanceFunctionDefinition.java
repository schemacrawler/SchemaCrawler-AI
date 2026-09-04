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
    Returns schema graph importance metrics, composite scores, counts, and traits for tables and
    views to identify key entities and structural hubs.
    The entry for each table includes:
    1) Importance score: composite integer (0-100) combining structural graph metrics (50%) and
    data-modeling attributes (50%)
    2) Importance metrics: graph topology metrics including in-degree, out-degree,
    betweenness centrality, and "impact reachability count"(blast radius for changes to the table)
    3) Table counts: Attribute column (columns with data values) count, total column_count, and
    foreign key, index, trigger, and row counts
    4) Table traits: Entity model type (strong entity, weak entity, subtype,
    bridge table, non entity) and boolean flags for whether primary_keys, foreign keys,
    indexes, has triggers are present, and whether the table has data
    Returns data as a JSON object.
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
