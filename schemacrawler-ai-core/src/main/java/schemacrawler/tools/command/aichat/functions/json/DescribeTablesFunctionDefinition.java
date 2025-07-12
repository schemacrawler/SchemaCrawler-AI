/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */


package schemacrawler.tools.command.aichat.functions.json;

public final class DescribeTablesFunctionDefinition
    extends AbstractJsonFunctionDefinition<DescribeTablesFunctionParameters> {

  @Override
  public String getDescription() {
    return """
        Get the details and description of database tables or views,
        including columns, primary key, foreign keys, indexes and triggers.
        This could return a lot of information if not limited by a
        parameter specifying one or more tables.
        Returns data as a JSON object.
        """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<DescribeTablesFunctionParameters> getParametersClass() {
    return DescribeTablesFunctionParameters.class;
  }

  @Override
  public DescribeTablesFunctionExecutor newExecutor() {
    return new DescribeTablesFunctionExecutor(getFunctionName());
  }
}
