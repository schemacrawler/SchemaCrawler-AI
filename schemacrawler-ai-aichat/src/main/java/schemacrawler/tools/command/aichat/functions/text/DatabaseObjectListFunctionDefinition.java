/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */



package schemacrawler.tools.command.aichat.functions.text;

public final class DatabaseObjectListFunctionDefinition
    extends AbstractTextFunctionDefinition<DatabaseObjectListFunctionParameters> {

  @Override
  public String getDescription() {
    return """
        List database objects like tables, routines
        (that is, functions and stored procedures), sequences, or synonyms.
        """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<DatabaseObjectListFunctionParameters> getParametersClass() {
    return DatabaseObjectListFunctionParameters.class;
  }

  @Override
  public DatabaseObjectListFunctionExecutor newExecutor() {
    return new DatabaseObjectListFunctionExecutor(getFunctionName());
  }
}
