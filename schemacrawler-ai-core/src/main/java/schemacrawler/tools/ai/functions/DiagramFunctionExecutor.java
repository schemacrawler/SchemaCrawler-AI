/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import java.nio.file.Path;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schemacrawler.FilterOptionsBuilder;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.ai.functions.DiagramFunctionParameters.DiagramType;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.base.AbstractExecutableFunctionExecutor;
import schemacrawler.tools.ai.tools.base.ExecutionParameters;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import us.fatehi.utility.property.PropertyName;

public final class DiagramFunctionExecutor
    extends AbstractExecutableFunctionExecutor<DiagramFunctionParameters> {

  protected DiagramFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public FunctionReturn call() {
    final DiagramType diagramType = commandOptions.diagramType();
    final Config additionalConfig = createAdditionalConfig(diagramType);
    final ExecutionParameters executionParameters =
        new ExecutionParameters(
            diagramType.getCommand(), additionalConfig, diagramType.getOutputFormatValue());
    final Path outputFilePath = execute(executionParameters);
    return returnText(outputFilePath);
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
    final FilterOptionsBuilder filterOptionsBuilder = FilterOptionsBuilder.builder();
    if (commandOptions.includeChildTables()) {
      filterOptionsBuilder.childTableFilterDepth(1);
    }
    if (commandOptions.includeReferencedTables()) {
      filterOptionsBuilder.parentTableFilterDepth(1);
    }

    final InclusionRule grepTablesInclusionRule = makeInclusionRule(commandOptions.tableName());
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder().includeGreppedTables(grepTablesInclusionRule);

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withFilterOptions(filterOptionsBuilder.build())
            .withGrepOptions(grepOptionsBuilder.toOptions());
    return schemaCrawlerOptions;
  }

  private Config createAdditionalConfig(final DiagramType diagramType) {
    final Config additionalConfig = ConfigUtility.newConfig();
    if (diagramType == null || diagramType == DiagramType.GRAPHVIZ) {
      return additionalConfig;
    }

    additionalConfig.put("script-language", "python");
    additionalConfig.put("script", diagramType.script());

    return additionalConfig;
  }
}
