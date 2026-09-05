/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.function.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static schemacrawler.tools.ai.functions.ListMembersOfTablesFunctionParameters.TableMemberType.COLUMNS;
import static schemacrawler.tools.ai.functions.ListMembersOfTablesFunctionParameters.TableMemberType.FOREIGN_KEYS;
import static schemacrawler.tools.ai.functions.ListMembersOfTablesFunctionParameters.TableMemberType.INDEXES;
import static schemacrawler.tools.ai.functions.ListMembersOfTablesFunctionParameters.TableMemberType.TRIGGERS;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.tools.ai.functions.ListMembersOfTablesFunctionDefinition;
import schemacrawler.tools.ai.functions.ListMembersOfTablesFunctionParameters;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionReturn;
import tools.jackson.core.type.TypeReference;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ListMembersOfTablesFunctionTest extends AbstractFunctionTest {

  @Test
  public void columns(final TestContext testContext) throws Exception {
    final ListMembersOfTablesFunctionParameters args =
        new ListMembersOfTablesFunctionParameters(COLUMNS, null, null);
    databaseObjects(testContext, args);
  }

  @Test
  public void columnsFilterIn(final TestContext testContext) throws Exception {
    // PUBLICATIONDATE column
    final ListMembersOfTablesFunctionParameters args =
        new ListMembersOfTablesFunctionParameters(COLUMNS, "PUBLICATION", null);
    databaseObjects(testContext, args);
  }

  @Test
  public void columnsFilterOut(final TestContext testContext) throws Exception {
    final ListMembersOfTablesFunctionParameters args =
        new ListMembersOfTablesFunctionParameters(COLUMNS, "NOT A COLUMN", null);
    databaseObjects(testContext, args);
  }

  @Test
  public void columnsForTable(final TestContext testContext) throws Exception {
    final ListMembersOfTablesFunctionParameters args =
        new ListMembersOfTablesFunctionParameters(COLUMNS, null, "AUTHORS");
    databaseObjects(testContext, args);
  }

  @Test
  public void foreignKeys(final TestContext testContext) throws Exception {
    final ListMembersOfTablesFunctionParameters args =
        new ListMembersOfTablesFunctionParameters(FOREIGN_KEYS, null, null);
    databaseObjects(testContext, args);
  }

  @Test
  public void indexes(final TestContext testContext) throws Exception {
    final ListMembersOfTablesFunctionParameters args =
        new ListMembersOfTablesFunctionParameters(INDEXES, null, null);
    databaseObjects(testContext, args);
  }

  @Test
  public void indexesFilterIn(final TestContext testContext) throws Exception {
    // U_PREVIOUSEDITION index
    final ListMembersOfTablesFunctionParameters args =
        new ListMembersOfTablesFunctionParameters(INDEXES, "U_PREVIOUS", null);
    databaseObjects(testContext, args);
  }

  @Test
  public void indexesFilterOut(final TestContext testContext) throws Exception {
    final ListMembersOfTablesFunctionParameters args =
        new ListMembersOfTablesFunctionParameters(INDEXES, "NOT AN INDEX", null);
    databaseObjects(testContext, args);
  }

  @Test
  public void indexesForTable(final TestContext testContext) throws Exception {
    final ListMembersOfTablesFunctionParameters args =
        new ListMembersOfTablesFunctionParameters(INDEXES, null, "AUTHORS");
    databaseObjects(testContext, args);
  }

  @Test
  public void none(final TestContext testContext) throws Exception {
    final ListMembersOfTablesFunctionParameters args =
        new ListMembersOfTablesFunctionParameters(null, null, null);
    databaseObjects(testContext, args);
  }

  @Test
  public void parameters() throws Exception {

    final ListMembersOfTablesFunctionParameters args =
        new ListMembersOfTablesFunctionParameters(COLUMNS, null, null);

    final Map<String, String> resultMap =
        mapper.readValue(args.toString(), new TypeReference<Map<String, String>>() {});

    assertThat(resultMap, hasEntry("member_type", "COLUMNS"));
  }

  @Test
  public void triggers(final TestContext testContext) throws Exception {
    final ListMembersOfTablesFunctionParameters args =
        new ListMembersOfTablesFunctionParameters(TRIGGERS, null, null);
    databaseObjects(testContext, args);
  }

  @Test
  public void triggersFilterIn(final TestContext testContext) throws Exception {
    // TRG_AUTHORS trigger
    final ListMembersOfTablesFunctionParameters args =
        new ListMembersOfTablesFunctionParameters(TRIGGERS, "TRG_AUTH", null);
    databaseObjects(testContext, args);
  }

  @Test
  public void triggersFilterOut(final TestContext testContext) throws Exception {
    final ListMembersOfTablesFunctionParameters args =
        new ListMembersOfTablesFunctionParameters(TRIGGERS, "NOT A TRIGGER", null);
    databaseObjects(testContext, args);
  }

  @Test
  public void triggersForTable(final TestContext testContext) throws Exception {
    final ListMembersOfTablesFunctionParameters args =
        new ListMembersOfTablesFunctionParameters(TRIGGERS, null, "AUTHORS");
    databaseObjects(testContext, args);
  }

  private void databaseObjects(
      final TestContext testContext, final ListMembersOfTablesFunctionParameters args)
      throws Exception {

    final ListMembersOfTablesFunctionDefinition functionDefinition =
        new ListMembersOfTablesFunctionDefinition();

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final FunctionExecutor<ListMembersOfTablesFunctionParameters> executor =
          functionDefinition.newExecutor();
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
