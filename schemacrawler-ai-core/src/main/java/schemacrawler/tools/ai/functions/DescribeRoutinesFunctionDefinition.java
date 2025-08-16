/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

public final class DescribeRoutinesFunctionDefinition
    extends AbstractJsonFunctionDefinition<DescribeRoutinesFunctionParameters> {

  @Override
  public String getDescription() {
    return """
        Get the details and description of database routine
        (stored procedures or functions),
        including parameters and return types.
        This could return a lot of information if not limited by a
        parameter specifying one or more routines.
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
  public DescribeRoutinesFunctionExecutor newExecutor() {
    return new DescribeRoutinesFunctionExecutor(getFunctionName(), getFunctionReturnType());
  }
}
