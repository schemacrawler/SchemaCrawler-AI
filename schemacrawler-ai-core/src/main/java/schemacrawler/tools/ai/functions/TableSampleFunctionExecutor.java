/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import java.nio.file.Path;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.base.AbstractExecutableFunctionExecutor;
import schemacrawler.tools.ai.tools.base.ExecutionParameters;
import us.fatehi.utility.property.PropertyName;

public final class TableSampleFunctionExecutor
    extends AbstractExecutableFunctionExecutor<TableSampleFunctionParameters> {

  protected TableSampleFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public FunctionReturn call() {
    final SchemaCrawlerOptions schemaCrawlerOptions = createSchemaCrawlerOptions();
    final ExecutionParameters executionParameters =
        new ExecutionParameters("tablesample", schemaCrawlerOptions, "json");
    final Path outputFilePath = execute(executionParameters);
    return returnJson(outputFilePath);
  }

  private SchemaCrawlerOptions createSchemaCrawlerOptions() {
    final InclusionRule grepTablesInclusionRule = makeInclusionRule(commandOptions.tableName());
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder().includeGreppedTables(grepTablesInclusionRule);
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withGrepOptions(grepOptionsBuilder.toOptions());
    return schemaCrawlerOptions;
  }
}
