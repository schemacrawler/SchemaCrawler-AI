/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.NoParameters;

public final class ServerInformationFunctionDefinition
    extends AbstractJsonFunctionDefinition<NoParameters> {

  @Override
  public String getDescription() {
    return """
    Provides more information about the database server, for example the product version,
    collation and so on. Details will vary based on the type of the server, for example,
    if it is Oracle, SQL Server, PostgreSQL, MySQL, etc.
    Returns JSON data.
    """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<NoParameters> getParametersClass() {
    return NoParameters.class;
  }

  @Override
  public ServerInformationFunctionExecutor newExecutor() {
    return new ServerInformationFunctionExecutor(getFunctionName(), getFunctionReturnType());
  }
}
