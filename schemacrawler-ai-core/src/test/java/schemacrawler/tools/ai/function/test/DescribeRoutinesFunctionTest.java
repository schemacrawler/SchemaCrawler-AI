/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.function.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.ai.functions.DescribeRoutinesFunctionParameters.RoutineDescriptionScope.DEFAULT;
import static schemacrawler.tools.ai.functions.DescribeRoutinesFunctionParameters.RoutineDescriptionScope.REFERENCED_OBJECTS;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.ai.functions.DescribeRoutinesFunctionDefinition;
import schemacrawler.tools.ai.functions.DescribeRoutinesFunctionParameters;
import schemacrawler.tools.ai.utility.test.FunctionExecutionTestUtility;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DescribeRoutinesFunctionTest extends AbstractFunctionTest {

  @Test
  public void describeAllRoutines(final TestContext testContext) throws Exception {
    final DescribeRoutinesFunctionParameters args =
        new DescribeRoutinesFunctionParameters(null, null);
    describeRoutine(testContext, args, true);
  }

  @Test
  public void describeReferencedObjects(final TestContext testContext) throws Exception {
    final DescribeRoutinesFunctionParameters args =
        new DescribeRoutinesFunctionParameters("GetBooksCount", List.of(REFERENCED_OBJECTS));
    describeRoutine(testContext, args, true);
  }

  @Test
  public void describeRoutine(final TestContext testContext) throws Exception {
    final DescribeRoutinesFunctionParameters args =
        new DescribeRoutinesFunctionParameters("GetBooksCount", null);
    describeRoutine(testContext, args, true);
  }

  @Test
  public void describeRoutineColumns(final TestContext testContext) throws Exception {
    final DescribeRoutinesFunctionParameters args =
        new DescribeRoutinesFunctionParameters("GetBooksCount", List.of(DEFAULT));
    describeRoutine(testContext, args, true);
  }

  @Test
  public void describeUnknownRoutine(final TestContext testContext) throws Exception {
    final DescribeRoutinesFunctionParameters args =
        new DescribeRoutinesFunctionParameters("NOT_A_ROUTINE", null);
    describeRoutine(testContext, args, true);
  }

  @Test
  public void parameters(final TestContext testContext) throws Exception {
    final DescribeRoutinesFunctionParameters args =
        new DescribeRoutinesFunctionParameters("GetBooksCount", null);

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.write(args.toString());
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  private void describeRoutine(
      final TestContext testContext,
      final DescribeRoutinesFunctionParameters args,
      final boolean hasResults)
      throws Exception {

    final DescribeRoutinesFunctionDefinition functionDefinition =
        new DescribeRoutinesFunctionDefinition();
    FunctionExecutionTestUtility.assertFunctionExecution(
        testContext, functionDefinition, args, catalog, null, hasResults);
  }
}
