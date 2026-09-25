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
    Detects table clusters - also known as communities, cliques, modules, or subsystems -
    of tightly related tables and views, based on how they are connected by foreign keys
    and other relationships. Use this tool to discover system domains, bounded contexts,
    or functional areas within a database schema, without having to know table names in
    advance. Table clusters can be filtered by a regular expression matching any member's
    fully qualified name. Each table cluster includes the full names of its member tables
    and views, and the anchor table (its most important, or representative, member).
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
    return "Detect table clusters";
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
