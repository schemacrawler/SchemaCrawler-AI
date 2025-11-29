/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
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
    Analyzes and validates database schema structure and design. Detects missing
    or redundant indexes, foreign keys without supporting indexes, and unique
    indexes with nullable columns. Checks data type consistency across similarly
    named columns, validates key patterns, and enforces naming conventions.
    Assesses design quality by inspecting documentation completeness, reserved
    word usage, and structural anomalies like single-column or all-nullable tables.
    Returns data as a JSON object.
    """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<LintFunctionParameters> getParametersClass() {
    return LintFunctionParameters.class;
  }

  @Override
  public String getTitle() {
    return "Validate database schema";
  }

  @Override
  public LintFunctionExecutor newExecutor() {
    return new LintFunctionExecutor(getFunctionName());
  }
}
