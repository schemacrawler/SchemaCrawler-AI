/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.function.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.ermodel.model.RelationshipCardinality;
import schemacrawler.tools.ai.functions.DescribeRelationshipsFunctionDefinition;
import schemacrawler.tools.ai.functions.DescribeRelationshipsFunctionParameters;
import schemacrawler.tools.ai.utility.test.FunctionExecutionTestUtility;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DescribeRelationshipsFunctionTest extends AbstractFunctionTest {

  @Test
  public void describe1NRelationships(final TestContext testContext) throws Exception {
    final DescribeRelationshipsFunctionParameters args =
        new DescribeRelationshipsFunctionParameters(
            "FK_SALES_REGIONS", RelationshipCardinality.one_many);
    describeRelationship(testContext, args, true);
  }

  @Test
  public void describeAllRelationships(final TestContext testContext) throws Exception {
    final DescribeRelationshipsFunctionParameters args =
        new DescribeRelationshipsFunctionParameters(null, null);
    describeRelationship(testContext, args, true);
  }

  @Test
  public void describeRelationship(final TestContext testContext) throws Exception {
    final DescribeRelationshipsFunctionParameters args =
        new DescribeRelationshipsFunctionParameters("FK_PREVIOUSEDITION", null);
    describeRelationship(testContext, args, true);
  }

  @Test
  public void describeUnknownRelationship(final TestContext testContext) throws Exception {
    final DescribeRelationshipsFunctionParameters args =
        new DescribeRelationshipsFunctionParameters("NOT_A_REL", null);
    describeRelationship(testContext, args, true);
  }

  private void describeRelationship(
      final TestContext testContext,
      final DescribeRelationshipsFunctionParameters args,
      final boolean hasResults)
      throws Exception {

    final DescribeRelationshipsFunctionDefinition functionDefinition =
        new DescribeRelationshipsFunctionDefinition();
    FunctionExecutionTestUtility.assertFunctionExecution(
        testContext, functionDefinition, args, catalog, erModel, null, hasResults);
  }
}
