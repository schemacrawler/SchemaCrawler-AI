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
import schemacrawler.tools.ai.functions.DiagramFunctionParameters.DiagramType;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.base.AbstractExecutableFunctionExecutor;
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
    final Path outputFilePath = execute("script", "text");
    return returnText(outputFilePath);
  }

  @Override
  protected Config createAdditionalConfig() {
    final Config additionalConfig = ConfigUtility.newConfig();

    final DiagramType diagramType = commandOptions.diagramType();
    additionalConfig.put("script-language", "python");
    additionalConfig.put("script", diagramType.script());
    return additionalConfig;
  }

  @Override
  protected InclusionRule grepTablesInclusionRule() {
    return makeInclusionRule(commandOptions.tableName());
  }
}
