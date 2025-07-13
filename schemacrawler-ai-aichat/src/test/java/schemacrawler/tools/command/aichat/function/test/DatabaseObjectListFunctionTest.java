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
import static schemacrawler.tools.ai.model.DatabaseObjectType.ALL;
import static schemacrawler.tools.ai.model.DatabaseObjectType.ROUTINES;
import static schemacrawler.tools.ai.model.DatabaseObjectType.SEQUENCES;
import static schemacrawler.tools.ai.model.DatabaseObjectType.SYNONYMS;
import static schemacrawler.tools.ai.model.DatabaseObjectType.TABLES;
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
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.command.aichat.functions.text.DatabaseObjectListFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.text.DatabaseObjectListFunctionParameters;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseObjectListFunctionTest {

  private Catalog catalog;

  @Test
  public void all(final TestContext testContext) throws Exception {
    final DatabaseObjectListFunctionParameters args = new DatabaseObjectListFunctionParameters(ALL);
    databaseObjects(testContext, args);
  }

  @BeforeAll
  public void loadCatalog(final Connection connection) throws Exception {

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
  public void parameters() throws Exception {
    final DatabaseObjectListFunctionParameters args = new DatabaseObjectListFunctionParameters(ALL);
    assertThat(args.toString(), is("{\"database-object-type\":\"ALL\"}"));
  }

  @Test
  public void routines(final TestContext testContext) throws Exception {
    final DatabaseObjectListFunctionParameters args =
        new DatabaseObjectListFunctionParameters(ROUTINES);
    databaseObjects(testContext, args);
  }

  @Test
  public void sequences(final TestContext testContext) throws Exception {
    final DatabaseObjectListFunctionParameters args =
        new DatabaseObjectListFunctionParameters(SEQUENCES);
    databaseObjects(testContext, args);
  }

  @Test
  public void synonyms(final TestContext testContext) throws Exception {
    final DatabaseObjectListFunctionParameters args =
        new DatabaseObjectListFunctionParameters(SYNONYMS);
    databaseObjects(testContext, args);
  }

  @Test
  public void tables(final TestContext testContext) throws Exception {
    final DatabaseObjectListFunctionParameters args =
        new DatabaseObjectListFunctionParameters(TABLES);
    databaseObjects(testContext, args);
  }

  private void databaseObjects(
      final TestContext testContext, final DatabaseObjectListFunctionParameters args)
      throws Exception {

    final DatabaseObjectListFunctionDefinition functionDefinition =
        new DatabaseObjectListFunctionDefinition();

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final FunctionExecutor<DatabaseObjectListFunctionParameters> executor =
          functionDefinition.newExecutor();
      executor.configure(args);
      executor.setCatalog(catalog);
      final FunctionReturn functionReturn = executor.call();
      out.write(functionReturn.get());
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
