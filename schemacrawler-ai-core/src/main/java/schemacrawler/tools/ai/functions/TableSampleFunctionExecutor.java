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
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.base.AbstractExecutableFunctionExecutor;
import us.fatehi.utility.property.PropertyName;

public final class TableSampleFunctionExecutor
    extends AbstractExecutableFunctionExecutor<TableSampleFunctionParameters> {

  protected TableSampleFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public FunctionReturn call() {
    final Path outputFilePath = execute("tablesample", "json");
    return returnJson(outputFilePath);
  }

  @Override
  protected InclusionRule grepTablesInclusionRule() {
    return makeInclusionRule(commandOptions.tableName());
  }
}
