/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.base.AbstractFunctionDefinition;

public final class DescribeErEntitiesFunctionDefinition
    extends AbstractFunctionDefinition<DescribeErEntitiesFunctionParameters> {

  @Override
  public String getDescription() {
    return """
    Generates detailed documentation for entities in the ER model, including
    entity type such as strong, weak and subtype entities, and attributes.
    Supports regex-based entity name filtering to optimize tool performance.
    Returns conceptual ER-model entities as a JSON object. For raw physical
    schema details such as columns, constraints, and DDL, use describe_tables.
    """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<DescribeErEntitiesFunctionParameters> getParametersClass() {
    return DescribeErEntitiesFunctionParameters.class;
  }

  @Override
  public String getTitle() {
    return "Describe entities in the ER model; use describe_tables for physical schema details";
  }

  @Override
  public DescribeErEntitiesFunctionExecutor newExecutor() {
    return new DescribeErEntitiesFunctionExecutor(getFunctionName());
  }

  @Override
  public DescribeErEntitiesFunctionParameters newParameters() {
    return new DescribeErEntitiesFunctionParameters();
  }
}
