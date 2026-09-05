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
import schemacrawler.tools.ai.functions.DescribeErEntitiesFunctionDefinition;
import schemacrawler.tools.ai.functions.DescribeErEntitiesFunctionParameters;
import schemacrawler.tools.ai.functions.DescribeErEntitiesFunctionParameters.EntityKind;
import schemacrawler.tools.ai.utility.test.FunctionExecutionTestUtility;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DescribeErEntitiesFunctionTest extends AbstractFunctionTest {

  @Test
  public void describeAllEntities(final TestContext testContext) throws Exception {
    final DescribeErEntitiesFunctionParameters args =
        new DescribeErEntitiesFunctionParameters(null, null);
    describeEntity(testContext, args, true);
  }

  @Test
  public void describeAssociations(final TestContext testContext) throws Exception {
    final DescribeErEntitiesFunctionParameters args =
        new DescribeErEntitiesFunctionParameters("AUTHORS", EntityKind.ASSOCIATION);
    describeEntity(testContext, args, true);
  }

  @Test
  public void describeEntity(final TestContext testContext) throws Exception {
    final DescribeErEntitiesFunctionParameters args =
        new DescribeErEntitiesFunctionParameters("AUTHORS", null);
    describeEntity(testContext, args, true);
  }

  @Test
  public void describeStrongEntities(final TestContext testContext) throws Exception {
    final DescribeErEntitiesFunctionParameters args =
        new DescribeErEntitiesFunctionParameters("BOOKS", EntityKind.STRONG_ENTITY);
    describeEntity(testContext, args, true);
  }

  @Test
  public void describeUnknownEntity(final TestContext testContext) throws Exception {
    final DescribeErEntitiesFunctionParameters args =
        new DescribeErEntitiesFunctionParameters("NOT_A_TABLE", null);
    describeEntity(testContext, args, true);
  }

  private void describeEntity(
      final TestContext testContext,
      final DescribeErEntitiesFunctionParameters args,
      final boolean hasResults)
      throws Exception {

    final DescribeErEntitiesFunctionDefinition functionDefinition =
        new DescribeErEntitiesFunctionDefinition();
    FunctionExecutionTestUtility.assertFunctionExecution(
        testContext, functionDefinition, args, catalog, erModel, null, hasResults);
  }
}
