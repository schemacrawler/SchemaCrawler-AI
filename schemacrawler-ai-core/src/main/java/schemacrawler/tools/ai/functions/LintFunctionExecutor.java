/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.tools.ai.tools.AbstractExecutableFunctionExecutor;
import us.fatehi.utility.property.PropertyName;

public final class LintFunctionExecutor
    extends AbstractExecutableFunctionExecutor<LintFunctionParameters> {

  protected LintFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  protected String getCommand() {
    return "lint";
  }

  @Override
  protected InclusionRule grepTablesInclusionRule() {
    return makeInclusionRule(commandOptions.tableName());
  }
}
