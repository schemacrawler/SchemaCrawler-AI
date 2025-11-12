/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.base.AbstractFunctionDefinition;

public final class LintFunctionDefinition
    extends AbstractFunctionDefinition<LintFunctionParameters> {

  @Override
  public String getDescription() {
    return """
    Does comprehensive database schema analysis and validation, combining index
    recommendations, schema validation, and design quality assessment into a single process. It
    performs index analysis by detecting foreign keys without supporting indexes, redundant
    indexes, unique indexes with nullable columns, and tables lacking indexes. It enforces
    schema validation by checking data type consistency across tables with identical column
    names, validating primary key and surrogate key patterns, and ensuring naming convention
    compliance. It evaluates design quality by assessing table documentation completeness,
    identifying reserved word usage, and flagging structural antipatterns such as single-column
    tables or all-nullable columns. Running this tool can help with database performance
    tuning, schema quality assurance, and architectural governance.
    """
        .stripIndent()
        .replace("\n", " ")
        .strip();
  }

  @Override
  public Class<LintFunctionParameters> getParametersClass() {
    return LintFunctionParameters.class;
  }

  @Override
  public LintFunctionExecutor newExecutor() {
    return new LintFunctionExecutor(getFunctionName());
  }
}
