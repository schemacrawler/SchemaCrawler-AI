/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.base.AbstractFunctionDefinition;

public final class DiagramFunctionDefinition
    extends AbstractFunctionDefinition<DiagramFunctionParameters> {

  @Override
  public String getDescription() {
    return """
    Generates a database diagram in the specified format.
    """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<DiagramFunctionParameters> getParametersClass() {
    return DiagramFunctionParameters.class;
  }

  @Override
  public String getTitle() {
    return "Generate database diagram";
  }

  @Override
  public DiagramFunctionExecutor newExecutor() {
    return new DiagramFunctionExecutor(getFunctionName());
  }
}
