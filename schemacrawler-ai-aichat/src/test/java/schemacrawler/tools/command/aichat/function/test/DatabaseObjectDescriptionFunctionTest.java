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
import static schemacrawler.tools.command.aichat.functions.text.DatabaseObjectDescriptionFunctionParameters.DatabaseObjectsScope.ROUTINES;
import static schemacrawler.tools.command.aichat.functions.text.DatabaseObjectDescriptionFunctionParameters.DatabaseObjectsScope.SEQUENCES;
import static schemacrawler.tools.command.aichat.functions.text.DatabaseObjectDescriptionFunctionParameters.DatabaseObjectsScope.SYNONYMS;
import static schemacrawler.tools.command.aichat.functions.text.DatabaseObjectDescriptionFunctionParameters.DatabaseObjectsScope.TABLES;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.aichat.functions.text.DatabaseObjectDescriptionFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.text.DatabaseObjectDescriptionFunctionParameters;
import schemacrawler.tools.command.aichat.tools.utility.FunctionExecutionTestUtility;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseObjectDescriptionFunctionTest extends AbstractFunctionTest {

  @Test
  public void describeAllRoutines(final TestContext testContext) throws Exception {
    final DatabaseObjectDescriptionFunctionParameters args =
        new DatabaseObjectDescriptionFunctionParameters(null, ROUTINES);
    describeDatabaseObject(testContext, args, true);
  }

  @Test
  public void describeAllTables(final TestContext testContext) throws Exception {
    final DatabaseObjectDescriptionFunctionParameters args =
        new DatabaseObjectDescriptionFunctionParameters(null, TABLES);
    describeDatabaseObject(testContext, args, true);
  }

  @Test
  public void describeNone(final TestContext testContext) throws Exception {
    final DatabaseObjectDescriptionFunctionParameters args =
        new DatabaseObjectDescriptionFunctionParameters(null, null);
    describeDatabaseObject(testContext, args, true);
  }

  @Test
  public void describeRoutines(final TestContext testContext) throws Exception {
    final DatabaseObjectDescriptionFunctionParameters args =
        new DatabaseObjectDescriptionFunctionParameters("CUSTOMADD", ROUTINES);
    describeDatabaseObject(testContext, args, true);
  }

  @Test
  public void describeSequences(final TestContext testContext) throws Exception {
    final DatabaseObjectDescriptionFunctionParameters args =
        new DatabaseObjectDescriptionFunctionParameters("PUBLISHER_ID_SEQ", SEQUENCES);
    describeDatabaseObject(testContext, args, true);
  }

  @Test
  public void describeSynonyms(final TestContext testContext) throws Exception {
    final DatabaseObjectDescriptionFunctionParameters args =
        new DatabaseObjectDescriptionFunctionParameters("PUBLICATIONS", SYNONYMS);
    describeDatabaseObject(testContext, args, true);
  }

  @Test
  public void describeUnknownDatabaseObject(final TestContext testContext) throws Exception {
    final DatabaseObjectDescriptionFunctionParameters args =
        new DatabaseObjectDescriptionFunctionParameters("NOT_A SYNONYM", SYNONYMS);
    describeDatabaseObject(testContext, args, true);
  }

  @Test
  public void parameters(final TestContext testContext) throws Exception {
    final DatabaseObjectDescriptionFunctionParameters args =
        new DatabaseObjectDescriptionFunctionParameters(null, ROUTINES);
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.write(args.toString());
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  private void describeDatabaseObject(
      final TestContext testContext,
      final DatabaseObjectDescriptionFunctionParameters args,
      final boolean hasResults)
      throws Exception {

    final DatabaseObjectDescriptionFunctionDefinition functionDefinition =
        new DatabaseObjectDescriptionFunctionDefinition();
    FunctionExecutionTestUtility.assertFunctionExecution(
        testContext, functionDefinition, args, catalog, null, hasResults);
  }
}
