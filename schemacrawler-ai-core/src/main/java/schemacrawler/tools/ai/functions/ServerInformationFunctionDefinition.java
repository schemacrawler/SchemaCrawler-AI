/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.NoParameters;
import schemacrawler.tools.ai.tools.base.AbstractFunctionDefinition;

public final class ServerInformationFunctionDefinition
    extends AbstractFunctionDefinition<NoParameters> {

  @Override
  public String getDescription() {
    return """
    Provides database environment and server configuration metadata, including
    engine type and version, collation, encoding, parameters, capabilities, and
    platform details. Adapts output to the specific database (such as Oracle, SQL
    Server, PostgreSQL and so on) to support platform-aware SQL generation and
    schema analysis.
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
  public String getTitle() {
    return "Show database server information";
  }

  @Override
  public ServerInformationFunctionExecutor newExecutor() {
    return new ServerInformationFunctionExecutor(getFunctionName());
  }
}
