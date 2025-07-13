/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */



package schemacrawler.tools.command.aichat.functions.text;

public final class LintFunctionDefinition
    extends AbstractTextFunctionDefinition<LintFunctionParameters> {

  @Override
  public String getDescription() {
    return """
        Lint database schemas.
        Find design issues with specific tables, or with the entire database.
        Find problems with database design, such as no indexes on foreign keys.
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
  public LintFunctionExecutor newExecutor() {
    return new LintFunctionExecutor(getFunctionName());
  }
}
