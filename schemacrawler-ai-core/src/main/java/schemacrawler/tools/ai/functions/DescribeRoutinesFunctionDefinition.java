/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.base.AbstractFunctionDefinition;

public final class DescribeRoutinesFunctionDefinition
    extends AbstractFunctionDefinition<DescribeRoutinesFunctionParameters> {

  @Override
  public String getDescription() {
    return """
    Generates detailed documentation for database routines (stored procedures and functions).
    It extracts detailed metadata including parameter definitions (input/ output parameters,
    data types, default values), return type specifications, dependencies on tables, views and
    other routines, and complete DDL definitions. This helps with code documentation and
    comprehension, business logic discovery, and dependency analysis. Tool parameters support
    filtering routine names by regular expressions and selective detail scopes to optimize tool
    performance.
    Returns data as a JSON object.
    """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<DescribeRoutinesFunctionParameters> getParametersClass() {
    return DescribeRoutinesFunctionParameters.class;
  }

  @Override
  public String getTitle() {
    return "Describe stored procedures and functions";
  }

  @Override
  public DescribeRoutinesFunctionExecutor newExecutor() {
    return new DescribeRoutinesFunctionExecutor(getFunctionName());
  }
}
