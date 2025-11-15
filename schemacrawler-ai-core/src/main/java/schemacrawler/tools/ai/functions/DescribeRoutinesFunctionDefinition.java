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
    Generates detailed documentation for database routines (stored procedures and
    functions), including parameter metadata (input/ output parameters, data types,
    default values), return types, dependencies (on tables, views, or other
    routines), and full DDL definitions. Supports regex-based routine name
    filtering and configurable detail levels to optimize tool performance.
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
