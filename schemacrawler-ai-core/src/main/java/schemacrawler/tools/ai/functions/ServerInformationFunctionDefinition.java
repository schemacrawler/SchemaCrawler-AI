/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.NoParameters;
import schemacrawler.tools.ai.tools.base.AbstractFunctionDefinition;

public final class ServerInformationFunctionDefinition
    extends AbstractFunctionDefinition<NoParameters> {

  @Override
  public String getDescription() {
    return """
    Provides database environment and server configuration information, delivering metadata
    such as database engine type and version, collation settings, character encoding,
    configuration parameters, server capabilities, and platform information. This helps with
    compatibility assessment, migration planning, performance tuning, and environment
    documentation, adapting its output to the specific database platform (Oracle, SQL Server,
    PostgreSQL, MySQL, etc.) to ensure relevant details. (Details may vary depending on the
    database platform.) This metadata provides essential context for generating SQL and for
    analyzing the schema with other tools, ensuring that queries and validations are tailored
    to the specific database environment.
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
