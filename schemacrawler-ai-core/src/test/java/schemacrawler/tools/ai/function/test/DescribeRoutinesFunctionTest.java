/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.function.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.tools.ai.functions.DescribeRoutinesFunctionParameters.RoutineDescriptionScope.DEFAULT;
import static schemacrawler.tools.ai.functions.DescribeRoutinesFunctionParameters.RoutineDescriptionScope.REFERENCED_OBJECTS;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.tools.ai.functions.DescribeRoutinesFunctionDefinition;
import schemacrawler.tools.ai.functions.DescribeRoutinesFunctionParameters;
import schemacrawler.tools.ai.utility.test.FunctionExecutionTestUtility;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

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
