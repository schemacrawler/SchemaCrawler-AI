/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

public final class ListAcrossTablesFunctionDefinition
    extends AbstractJsonFunctionDefinition<ListAcrossTablesFunctionParameters> {

  @Override
  public String getDescription() {
    return """
        List names of names of database objects like columns, indexes, triggers,
        and foreign keys for tables across the whole database.
        The corresponding tables are identified, and details can be
        obtained later by describing those tables.
        Returns JSON data.
        """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<ListAcrossTablesFunctionParameters> getParametersClass() {
    return ListAcrossTablesFunctionParameters.class;
  }

  @Override
  public ListAcrossTablesFunctionExecutor newExecutor() {
    return new ListAcrossTablesFunctionExecutor(getFunctionName(), getFunctionReturnType());
  }
}
