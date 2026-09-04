/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import java.util.List;
import java.util.regex.Pattern;
import schemacrawler.importance.report.ImportanceReportEntry;
import schemacrawler.importance.report.ImportanceReportGenerator;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import schemacrawler.tools.ai.tools.base.AbstractJsonFunctionExecutor;
import tools.jackson.databind.node.ArrayNode;
import us.fatehi.utility.property.PropertyName;

public final class TableImportanceFunctionExecutor
    extends AbstractJsonFunctionExecutor<TableImportanceFunctionParameters> {

  protected TableImportanceFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public JsonFunctionReturn call() {
    final List<ImportanceReportEntry> entries =
        new ImportanceReportGenerator(requireSchemaGraphModel())
            .report(makeTableInclusionRule(commandOptions.tableName()), commandOptions.maxTables());
    final ArrayNode importance = mapper.valueToTree(entries);
    return new JsonFunctionReturn("importance", importance)
        .withSummary(
            "Returned importance metrics for %d tables and views".formatted(entries.size()));
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
  }

  private InclusionRule makeTableInclusionRule(final String tableName) {
    if (tableName == null || tableName.isBlank()) {
      return new IncludeAll();
    }
    return new RegularExpressionInclusionRule(Pattern.compile(tableName));
  }

  private schemacrawler.importance.model.SchemaGraphModel requireSchemaGraphModel() {
    return requireNonNull(getSchemaGraphModel(), "No schema graph model provided");
  }
}
