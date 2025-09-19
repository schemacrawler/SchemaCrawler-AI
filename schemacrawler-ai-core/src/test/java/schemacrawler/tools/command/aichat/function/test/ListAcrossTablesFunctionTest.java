/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.function.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.ai.functions.ListAcrossTablesFunctionParameters.DependantObjectType.COLUMNS;
import static schemacrawler.tools.ai.functions.ListAcrossTablesFunctionParameters.DependantObjectType.FOREIGN_KEYS;
import static schemacrawler.tools.ai.functions.ListAcrossTablesFunctionParameters.DependantObjectType.INDEXES;
import static schemacrawler.tools.ai.functions.ListAcrossTablesFunctionParameters.DependantObjectType.NONE;
import static schemacrawler.tools.ai.functions.ListAcrossTablesFunctionParameters.DependantObjectType.TRIGGERS;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.ai.functions.ListAcrossTablesFunctionDefinition;
import schemacrawler.tools.ai.functions.ListAcrossTablesFunctionParameters;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionReturn;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ListAcrossTablesFunctionTest extends AbstractFunctionTest {

  @Test
  public void columns(final TestContext testContext) throws Exception {
    final ListAcrossTablesFunctionParameters args =
        new ListAcrossTablesFunctionParameters(COLUMNS, null, null);
    databaseObjects(testContext, args);
  }

  @Test
  public void columnsFilterIn(final TestContext testContext) throws Exception {
    // PUBLICATIONDATE column
    final ListAcrossTablesFunctionParameters args =
        new ListAcrossTablesFunctionParameters(COLUMNS, "PUBLICATION", null);
    databaseObjects(testContext, args);
  }

  @Test
  public void columnsFilterOut(final TestContext testContext) throws Exception {
    final ListAcrossTablesFunctionParameters args =
        new ListAcrossTablesFunctionParameters(COLUMNS, "NOT A COLUMN", null);
    databaseObjects(testContext, args);
  }

  @Test
  public void columnsForTable(final TestContext testContext) throws Exception {
    final ListAcrossTablesFunctionParameters args =
        new ListAcrossTablesFunctionParameters(COLUMNS, null, "AUTHORS");
    databaseObjects(testContext, args);
  }

  @Test
  public void foreignKeys(final TestContext testContext) throws Exception {
    final ListAcrossTablesFunctionParameters args =
        new ListAcrossTablesFunctionParameters(FOREIGN_KEYS, null, null);
    databaseObjects(testContext, args);
  }

  @Test
  public void indexes(final TestContext testContext) throws Exception {
    final ListAcrossTablesFunctionParameters args =
        new ListAcrossTablesFunctionParameters(INDEXES, null, null);
    databaseObjects(testContext, args);
  }

  @Test
  public void indexesFilterIn(final TestContext testContext) throws Exception {
    // U_PREVIOUSEDITION index
    final ListAcrossTablesFunctionParameters args =
        new ListAcrossTablesFunctionParameters(INDEXES, "U_PREVIOUS", null);
    databaseObjects(testContext, args);
  }

  @Test
  public void indexesFilterOut(final TestContext testContext) throws Exception {
    final ListAcrossTablesFunctionParameters args =
        new ListAcrossTablesFunctionParameters(INDEXES, "NOT AN INDEX", null);
    databaseObjects(testContext, args);
  }

  @Test
  public void indexesForTable(final TestContext testContext) throws Exception {
    final ListAcrossTablesFunctionParameters args =
        new ListAcrossTablesFunctionParameters(INDEXES, null, "AUTHORS");
    databaseObjects(testContext, args);
  }

  @Test
  public void none(final TestContext testContext) throws Exception {
    final ListAcrossTablesFunctionParameters args =
        new ListAcrossTablesFunctionParameters(null, null, null);
    databaseObjects(testContext, args);
  }

  @Test
  public void parameters() throws Exception {

    final ListAcrossTablesFunctionParameters args =
        new ListAcrossTablesFunctionParameters(NONE, null, null);

    final Map<String, String> resultMap =
        mapper.readValue(args.toString(), new TypeReference<Map<String, String>>() {});

    assertThat(resultMap, hasEntry("dependant-object-type", "NONE"));
    assertThat(resultMap, hasEntry("dependant-object-name", null));
    assertThat(resultMap, hasEntry("table-name", null));
  }

  @Test
  public void triggers(final TestContext testContext) throws Exception {
    final ListAcrossTablesFunctionParameters args =
        new ListAcrossTablesFunctionParameters(TRIGGERS, null, null);
    databaseObjects(testContext, args);
  }

  @Test
  public void triggersFilterIn(final TestContext testContext) throws Exception {
    // TRG_AUTHORS trigger
    final ListAcrossTablesFunctionParameters args =
        new ListAcrossTablesFunctionParameters(TRIGGERS, "TRG_AUTH", null);
    databaseObjects(testContext, args);
  }

  @Test
  public void triggersFilterOut(final TestContext testContext) throws Exception {
    final ListAcrossTablesFunctionParameters args =
        new ListAcrossTablesFunctionParameters(TRIGGERS, "NOT A TRIGGER", null);
    databaseObjects(testContext, args);
  }

  @Test
  public void triggersForTable(final TestContext testContext) throws Exception {
    final ListAcrossTablesFunctionParameters args =
        new ListAcrossTablesFunctionParameters(TRIGGERS, null, "AUTHORS");
    databaseObjects(testContext, args);
  }

  private void databaseObjects(
      final TestContext testContext, final ListAcrossTablesFunctionParameters args)
      throws Exception {

    final ListAcrossTablesFunctionDefinition functionDefinition =
        new ListAcrossTablesFunctionDefinition();

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final FunctionExecutor<ListAcrossTablesFunctionParameters> executor =
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
