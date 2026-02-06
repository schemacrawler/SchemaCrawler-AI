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
import schemacrawler.ermodel.model.EntityType;
import schemacrawler.tools.ai.functions.DescribeEntitiesFunctionDefinition;
import schemacrawler.tools.ai.functions.DescribeEntitiesFunctionParameters;
import schemacrawler.tools.ai.utility.test.FunctionExecutionTestUtility;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DescribeEntitiesFunctionTest extends AbstractFunctionTest {

  @Test
  public void describeAllEntities(final TestContext testContext) throws Exception {
    final DescribeEntitiesFunctionParameters args =
        new DescribeEntitiesFunctionParameters(null, null);
    describeEntity(testContext, args, true);
  }

  @Test
  public void describeEntity(final TestContext testContext) throws Exception {
    final DescribeEntitiesFunctionParameters args =
        new DescribeEntitiesFunctionParameters("AUTHORS", null);
    describeEntity(testContext, args, true);
  }

  @Test
  public void describeStrongEntities(final TestContext testContext) throws Exception {
    final DescribeEntitiesFunctionParameters args =
        new DescribeEntitiesFunctionParameters("BOOKS", EntityType.strong_entity);
    describeEntity(testContext, args, true);
  }

  @Test
  public void describeUnknownEntity(final TestContext testContext) throws Exception {
    final DescribeEntitiesFunctionParameters args =
        new DescribeEntitiesFunctionParameters("NOT_A_TABLE", null);
    describeEntity(testContext, args, true);
  }

  private void describeEntity(
      final TestContext testContext,
      final DescribeEntitiesFunctionParameters args,
      final boolean hasResults)
      throws Exception {

    final DescribeEntitiesFunctionDefinition functionDefinition =
        new DescribeEntitiesFunctionDefinition();
    FunctionExecutionTestUtility.assertFunctionExecution(
        testContext, functionDefinition, args, catalog, erModel, null, hasResults);
  }
}
