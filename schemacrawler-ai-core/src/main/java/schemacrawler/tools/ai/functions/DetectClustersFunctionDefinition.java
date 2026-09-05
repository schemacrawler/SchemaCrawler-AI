/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.base.AbstractFunctionDefinition;

public final class DetectClustersFunctionDefinition
    extends AbstractFunctionDefinition<DetectClustersFunctionParameters> {

  @Override
  public String getDescription() {
    return """
    Returns communities or clusters of related tables and views. Communities can be selected
    by a regular expression matching any member's fully qualified name. Each community includes
    its full names of the tables in the community, and the anchor table.
    """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<DetectClustersFunctionParameters> getParametersClass() {
    return DetectClustersFunctionParameters.class;
  }

  @Override
  public String getTitle() {
    return "Detect table and view clusters";
  }

  @Override
  public DetectClustersFunctionExecutor newExecutor() {
    return new DetectClustersFunctionExecutor(getFunctionName());
  }

  @Override
  public DetectClustersFunctionParameters newParameters() {
    return new DetectClustersFunctionParameters();
  }
}
