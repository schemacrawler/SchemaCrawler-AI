/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

public final class DatabaseInformationFunctionDefinition
    extends AbstractJsonFunctionDefinition<DatabaseInformationFunctionParameters> {

  @Override
  public String getDescription() {
    return """
    Provides more information about the database server, for example the product version,
    collation and so on. Details will vary based on the type of the server, for example,
    if it is Oracle, SQL Server, PostgreSQL, MySQL, and so on.
    Returns JSON data.
    """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<DatabaseInformationFunctionParameters> getParametersClass() {
    return DatabaseInformationFunctionParameters.class;
  }

  @Override
  public DatabaseInformationFunctionExecutor newExecutor() {
    return new DatabaseInformationFunctionExecutor(getFunctionName(), getFunctionReturnType());
  }
}
