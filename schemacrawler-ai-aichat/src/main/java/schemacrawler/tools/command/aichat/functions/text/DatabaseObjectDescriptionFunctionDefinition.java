/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.functions.text;

public final class DatabaseObjectDescriptionFunctionDefinition
    extends AbstractTextFunctionDefinition<DatabaseObjectDescriptionFunctionParameters> {

  @Override
  public String getDescription() {
    return """
    Get the details and description of database objects like
    tables (all types, including views),
    routines (that is, functions and stored procedures),
    sequences, or synonyms.
    """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<DatabaseObjectDescriptionFunctionParameters> getParametersClass() {
    return DatabaseObjectDescriptionFunctionParameters.class;
  }

  @Override
  public DatabaseObjectDescriptionFunctionExecutor newExecutor() {
    return new DatabaseObjectDescriptionFunctionExecutor(getFunctionName());
  }
}
