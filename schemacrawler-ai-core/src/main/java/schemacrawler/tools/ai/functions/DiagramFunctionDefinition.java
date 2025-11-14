/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.base.AbstractFunctionDefinition;

public final class DiagramFunctionDefinition
    extends AbstractFunctionDefinition<DiagramFunctionParameters> {

  @Override
  public String getDescription() {
    return """
    Generates an editable database diagram, and makes it available as an embedded
    resource to the client.
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
    return "Generate editable database diagram";
  }

  @Override
  public DiagramFunctionExecutor newExecutor() {
    return new DiagramFunctionExecutor(getFunctionName());
  }
}
