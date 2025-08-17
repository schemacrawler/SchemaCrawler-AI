/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.function.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters.TableDescriptionScope.INDEXES;
import static schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters.TableDescriptionScope.PRIMARY_KEY;
import static schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters.TableDescriptionScope.REFERENCED_TABLES;
import static schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters.TableDescriptionScope.TRIGGERS;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.ai.functions.DescribeTablesFunctionDefinition;
import schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters;
import schemacrawler.tools.command.aichat.tools.utility.FunctionExecutionTestUtility;

@WithTestDatabase
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
