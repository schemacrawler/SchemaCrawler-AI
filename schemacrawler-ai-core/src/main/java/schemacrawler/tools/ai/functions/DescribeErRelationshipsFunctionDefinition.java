/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.base.AbstractFunctionDefinition;

public final class DescribeErRelationshipsFunctionDefinition
    extends AbstractFunctionDefinition<DescribeErRelationshipsFunctionParameters> {

  @Override
  public String getDescription() {
    return """
      Generates detailed documentation for relationships in the ER model, including
      1..1, 1..M, M..N and optional relationships.
      Supports regular expression based relationship name filtering to optimize
      tool performance.
      Returns conceptual ER-model relationships. For raw physical
      schema details such as columns, constraints, and DDL, use `describe_tables`.
      Returns data as a JSON object.
    """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<DescribeErRelationshipsFunctionParameters> getParametersClass() {
    return DescribeErRelationshipsFunctionParameters.class;
  }

  @Override
  public String getTitle() {
    return "Describe relationships in the ER model";
  }

  @Override
  public DescribeErRelationshipsFunctionExecutor newExecutor() {
    return new DescribeErRelationshipsFunctionExecutor(getFunctionName());
  }

  @Override
  public DescribeErRelationshipsFunctionParameters newParameters() {
    return new DescribeErRelationshipsFunctionParameters();
  }
}
