/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */


package schemacrawler.tools.command.aichat.function.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.command.aichat.options.DatabaseObjectType.ALL;
import static schemacrawler.tools.command.aichat.options.DatabaseObjectType.ROUTINES;
import static schemacrawler.tools.command.aichat.options.DatabaseObjectType.SEQUENCES;
import static schemacrawler.tools.command.aichat.options.DatabaseObjectType.SYNONYMS;
import static schemacrawler.tools.command.aichat.options.DatabaseObjectType.TABLES;
import java.sql.Connection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.aichat.functions.json.ListFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.json.ListFunctionParameters;
import schemacrawler.tools.command.aichat.tools.FunctionExecutor;
import schemacrawler.tools.command.aichat.tools.FunctionReturn;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ListFunctionTest {

  private Catalog catalog;

  @BeforeAll
  public void _loadCatalog(final Connection connection) throws Exception {

    final SchemaRetrievalOptions schemaRetrievalOptions = TestUtility.newSchemaRetrievalOptions();

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"))
            .includeAllSynonyms()
            .includeAllSequences()
            .includeAllRoutines();
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    catalog = getCatalog(connection, schemaRetrievalOptions, schemaCrawlerOptions);
  }

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
  public void parameters() throws Exception {
    final ListFunctionParameters args = new ListFunctionParameters(ALL, null);
    assertThat(
        args.toString(), is("{\"database-object-type\":\"ALL\",\"database-object-name\":null}"));
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
