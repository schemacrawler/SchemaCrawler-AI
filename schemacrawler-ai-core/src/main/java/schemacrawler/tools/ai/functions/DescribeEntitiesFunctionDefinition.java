/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.base.AbstractFunctionDefinition;

public final class DescribeEntitiesFunctionDefinition
    extends AbstractFunctionDefinition<DescribeEntitiesFunctionParameters> {

  @Override
  public String getDescription() {
    return """
    Generates detailed documentation for entities in the ER model, including
    entity type such as strong, weak and subtype entities, and attributes.
    Supports regex-based entity name filtering to optimize tool performance.
    Returns data as a JSON object.
    """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<DescribeEntitiesFunctionParameters> getParametersClass() {
    return DescribeEntitiesFunctionParameters.class;
  }

  @Override
  public String getTitle() {
    return "Describe entities in the ER model";
  }

  @Override
  public DescribeEntitiesFunctionExecutor newExecutor() {
    return new DescribeEntitiesFunctionExecutor(getFunctionName());
  }
}
