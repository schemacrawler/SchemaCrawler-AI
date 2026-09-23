/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.function.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static schemacrawler.tools.ai.model.DatabaseObjectType.ALL;
import static schemacrawler.tools.ai.model.DatabaseObjectType.ROUTINES;
import static schemacrawler.tools.ai.model.DatabaseObjectType.SCHEMAS;
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
  public void dbSchemas(final TestContext testContext) throws Exception {
    final ListFunctionParameters args = new ListFunctionParameters(SCHEMAS, null);
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

  /**
   * The "PUBLISHER SALES" schema requires quoting for display, since it contains a space. The
   * documented contract for `databaseObjectName` is that it "may match the fully qualified database
   * object name (including the schema)". An unquoted schema-qualified pattern like "PUBLISHER
   * SALES.REGIONS" matches the *unquoted* full name ("PUBLIC.PUBLISHER SALES.REGIONS") as well as
   * the *quoted* full name (PUBLIC."PUBLISHER SALES".REGIONS) that ListFunctionExecutor tests
   * against, because ListFunctionExecutor filters using SchemaCrawler's "limit" options
   * (LimitOptionsBuilder -> DatabaseObjectFilter), which is now quote-tolerant - consistent with
   * the "grep" options used by most other AI function executors.
   */
  @Test
  public void someTablesInQuotedSchema(final TestContext testContext) throws Exception {
    final ListFunctionParameters args =
        new ListFunctionParameters(TABLES, "PUBLISHER SALES.REGIONS");

    final ListFunctionDefinition functionDefinition = new ListFunctionDefinition();
    final FunctionExecutor<ListFunctionParameters> executor = functionDefinition.newExecutor();
    executor.configure(args);
    executor.setCatalog(catalog);
    executor.setERModel(erModel);
    final FunctionReturn functionReturn = executor.call();

    assertThat(functionReturn.get(), containsString("REGIONS"));
  }

  private void databaseObjects(final TestContext testContext, final ListFunctionParameters args)
      throws Exception {

    final ListFunctionDefinition functionDefinition = new ListFunctionDefinition();

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final FunctionExecutor<ListFunctionParameters> executor = functionDefinition.newExecutor();
      executor.configure(args);
      executor.setCatalog(catalog);
      executor.setERModel(erModel);
      final FunctionReturn functionReturn = executor.call();
      out.write(functionReturn.get());
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
