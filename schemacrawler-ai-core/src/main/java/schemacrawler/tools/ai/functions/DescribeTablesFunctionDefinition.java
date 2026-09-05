/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.tools.ai.tools.base.AbstractFunctionDefinition;

public final class DescribeTablesFunctionDefinition
    extends AbstractFunctionDefinition<DescribeTablesFunctionParameters> {

  @Override
  public String getDescription() {
    return """
    Generates detailed documentation for database tables and views, including
    column definitions (names, data types, constraints, nullability), primary and
    foreign key relationships, index and trigger information, table attributes,
    and complete DDL definitions. Supports regex-based table name filtering and
    configurable detail levels to optimize tool performance. For business-domain
    ER concepts such as entity type or relationship cardinality, use
    describe_er_entities or describe_er_relationships.
    Returns physical schema data as a JSON object.
    """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public Class<DescribeTablesFunctionParameters> getParametersClass() {
    return DescribeTablesFunctionParameters.class;
  }

  @Override
  public String getTitle() {
    return "Describe tables and views";
  }

  @Override
  public DescribeTablesFunctionExecutor newExecutor() {
    return new DescribeTablesFunctionExecutor(getFunctionName());
  }

  @Override
  public DescribeTablesFunctionParameters newParameters() {
    return new DescribeTablesFunctionParameters();
  }
}
