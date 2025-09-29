/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.AbstractFunctionDefinition;

public final class ListFunctionDefinition
    extends AbstractFunctionDefinition<ListFunctionParameters> {

  @Override
  public String getDescription() {
    return """
    List names of database objects like tables, routines
    (that is, functions and stored procedures), sequences, or synonyms.
    Returns JSON data.
    """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<ListFunctionParameters> getParametersClass() {
    return ListFunctionParameters.class;
  }

  @Override
  public ListFunctionExecutor newExecutor() {
    return new ListFunctionExecutor(getFunctionName());
  }
}
