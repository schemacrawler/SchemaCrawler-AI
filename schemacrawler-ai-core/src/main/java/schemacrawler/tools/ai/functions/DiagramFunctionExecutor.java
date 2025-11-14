/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.tools.ai.functions.DiagramFunctionParameters.DiagramType;
import schemacrawler.tools.ai.tools.base.AbstractExecutableFunctionExecutor;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.property.PropertyName;

public final class DiagramFunctionExecutor
    extends AbstractExecutableFunctionExecutor<DiagramFunctionParameters> {

  protected DiagramFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  protected String getCommand() {
    return "script";
  }

  @Override
  protected InclusionRule grepTablesInclusionRule() {
    return makeInclusionRule(commandOptions.tableName());
  }

  @Override
  protected Config createAdditionalConfig() {
    final Config additionalConfig = super.createAdditionalConfig();

    final DiagramType diagramType = commandOptions.diagramType();
    additionalConfig.put("script-language", "python");
    additionalConfig.put("script", diagramType.script());
    return additionalConfig;
  }
}
