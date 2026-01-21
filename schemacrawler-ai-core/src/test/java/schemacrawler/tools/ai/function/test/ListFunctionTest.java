/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.function.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.tools.ai.model.DatabaseObjectType.ALL;
import static schemacrawler.tools.ai.model.DatabaseObjectType.ROUTINES;
import static schemacrawler.tools.ai.model.DatabaseObjectType.SEQUENCES;
import static schemacrawler.tools.ai.model.DatabaseObjectType.SYNONYMS;
import static schemacrawler.tools.ai.model.DatabaseObjectType.TABLES;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.tools.ai.functions.ListFunctionDefinition;
import schemacrawler.tools.ai.functions.ListFunctionParameters;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionReturn;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ListFunctionTest extends AbstractFunctionTest {

  @Test
  public void all(final TestContext testContext) throws Exception {
    final ListFunctionParameters args = new ListFunctionParameters(ALL, null);
    databaseObjects(testContext, args);
  }

  @Test
  public void dbRoutines(final TestContext testContext) throws Exception {
    final ListFunctionParameters args = new ListFunctionParameters(ROUTINES, null);
    databaseObjects(testContext, args);
  }

  @Test
  public void dbSequences(final TestContext testContext) throws Exception {
    final ListFunctionParameters args = new ListFunctionParameters(SEQUENCES, null);
    databaseObjects(testContext, args);
  }

  @Test
  public void dbSynonyms(final TestContext testContext) throws Exception {
    final ListFunctionParameters args = new ListFunctionParameters(SYNONYMS, null);
    databaseObjects(testContext, args);
  }

  @Test
  public void dbTables(final TestContext testContext) throws Exception {
    final ListFunctionParameters args = new ListFunctionParameters(TABLES, null);
    databaseObjects(testContext, args);
  }

  @Test
  public void parameters(final TestContext testContext) throws Exception {
    final ListFunctionParameters args = new ListFunctionParameters(ALL, null);

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.write(args.toString());
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  @Test
  public void someRoutines(final TestContext testContext) throws Exception {
    final ListFunctionParameters args = new ListFunctionParameters(ROUTINES, "CUSTOMADD");
    databaseObjects(testContext, args);
  }

  @Test
  public void someTables(final TestContext testContext) throws Exception {
    final ListFunctionParameters args = new ListFunctionParameters(TABLES, "AUTHORS");
    databaseObjects(testContext, args);
  }

  private void databaseObjects(final TestContext testContext, final ListFunctionParameters args)
      throws Exception {

    final ListFunctionDefinition functionDefinition = new ListFunctionDefinition();

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final FunctionExecutor<ListFunctionParameters> executor = functionDefinition.newExecutor();
      executor.configure(args);
      executor.setCatalog(catalog);
      final FunctionReturn functionReturn = executor.call();
      out.write(functionReturn.get());
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
