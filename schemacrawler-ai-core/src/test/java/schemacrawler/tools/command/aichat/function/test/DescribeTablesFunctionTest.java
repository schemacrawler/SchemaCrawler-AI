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
import static schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters.TableDescriptionScope.CHILD_TABLES;
import static schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters.TableDescriptionScope.INDEXES;
import static schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters.TableDescriptionScope.PRIMARY_KEY;
import static schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters.TableDescriptionScope.TRIGGERS;
import java.sql.Connection;
import java.util.List;
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
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.ai.functions.DescribeTablesFunctionDefinition;
import schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters;
import schemacrawler.tools.command.aichat.tools.utility.FunctionExecutionTestUtility;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DescribeTablesFunctionTest {

  private Catalog catalog;

  @Test
  public void describeAllTables(final TestContext testContext) throws Exception {
    final DescribeTablesFunctionParameters args = new DescribeTablesFunctionParameters(null, null);
    describeTable(testContext, args, true);
  }

  @Test
  public void describeChildTables(final TestContext testContext) throws Exception {
    final DescribeTablesFunctionParameters args =
        new DescribeTablesFunctionParameters("BOOKS", List.of(CHILD_TABLES));
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
  public void parameters(final TestContext testContext) throws Exception {
    final DescribeTablesFunctionParameters args =
        new DescribeTablesFunctionParameters("AUTHORS", null);
    assertThat(args.toString(), is("{\"table-name\":\"AUTHORS\",\"description-scope\":[]}"));
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
