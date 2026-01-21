/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.function.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters.TableDescriptionScope.INDEXES;
import static schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters.TableDescriptionScope.PRIMARY_KEY;
import static schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters.TableDescriptionScope.REFERENCED_TABLES;
import static schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters.TableDescriptionScope.TRIGGERS;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.tools.ai.functions.DescribeTablesFunctionDefinition;
import schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters;
import schemacrawler.tools.ai.utility.test.FunctionExecutionTestUtility;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DescribeTablesFunctionTest extends AbstractFunctionTest {

  @Test
  public void describeAllTables(final TestContext testContext) throws Exception {
    final DescribeTablesFunctionParameters args = new DescribeTablesFunctionParameters(null, null);
    describeTable(testContext, args, true);
  }

  @Test
  public void describeReferencedTables(final TestContext testContext) throws Exception {
    final DescribeTablesFunctionParameters args =
        new DescribeTablesFunctionParameters("BOOKS", List.of(REFERENCED_TABLES));
    describeTable(testContext, args, true);
  }

  @Test
  public void describeTable(final TestContext testContext) throws Exception {
    final DescribeTablesFunctionParameters args =
        new DescribeTablesFunctionParameters("AUTHORS", null);
    describeTable(testContext, args, true);
  }

  @Test
  public void describeTableColumns(final TestContext testContext) throws Exception {
    final DescribeTablesFunctionParameters args =
        new DescribeTablesFunctionParameters("ΒΙΒΛΊΑ", null);
    describeTable(testContext, args, true);
  }

  @Test
  public void describeTableForeignKeys(final TestContext testContext) throws Exception {
    final DescribeTablesFunctionParameters args =
        new DescribeTablesFunctionParameters("BOOKAUTHORS", null);
    describeTable(testContext, args, true);
  }

  @Test
  public void describeTableIndexes(final TestContext testContext) throws Exception {
    final DescribeTablesFunctionParameters args =
        new DescribeTablesFunctionParameters("BOOKAUTHORS", List.of(INDEXES));
    describeTable(testContext, args, true);
  }

  @Test
  public void describeTablePrimaryKey(final TestContext testContext) throws Exception {
    final DescribeTablesFunctionParameters args =
        new DescribeTablesFunctionParameters("AUTHORS", List.of(PRIMARY_KEY));
    describeTable(testContext, args, true);
  }

  @Test
  public void describeTableTriggers(final TestContext testContext) throws Exception {
    final DescribeTablesFunctionParameters args =
        new DescribeTablesFunctionParameters("AUTHORS", List.of(TRIGGERS));
    describeTable(testContext, args, true);
  }

  @Test
  public void describeUnknownTable(final TestContext testContext) throws Exception {
    final DescribeTablesFunctionParameters args =
        new DescribeTablesFunctionParameters("NOT_A_TABLE", null);
    describeTable(testContext, args, true);
  }

  @Test
  public void describeView(final TestContext testContext) throws Exception {
    final DescribeTablesFunctionParameters args =
        new DescribeTablesFunctionParameters("AuthorsList", null);
    describeTable(testContext, args, true);
  }

  @Test
  public void parameters(final TestContext testContext) throws Exception {
    final DescribeTablesFunctionParameters args =
        new DescribeTablesFunctionParameters("AUTHORS", null);

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.write(args.toString());
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  private void describeTable(
      final TestContext testContext,
      final DescribeTablesFunctionParameters args,
      final boolean hasResults)
      throws Exception {

    final DescribeTablesFunctionDefinition functionDefinition =
        new DescribeTablesFunctionDefinition();
    FunctionExecutionTestUtility.assertFunctionExecution(
        testContext, functionDefinition, args, catalog, null, hasResults);
  }
}
