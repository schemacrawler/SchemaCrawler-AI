/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.aichat.function.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.tools.command.aichat.functions.TableDecriptionFunctionParameters.TableDescriptionScope.COLUMNS;
import static schemacrawler.tools.command.aichat.functions.TableDecriptionFunctionParameters.TableDescriptionScope.FOREIGN_KEYS;
import static schemacrawler.tools.command.aichat.functions.TableDecriptionFunctionParameters.TableDescriptionScope.INDEXES;
import static schemacrawler.tools.command.aichat.functions.TableDecriptionFunctionParameters.TableDescriptionScope.PRIMARY_KEY;
import static schemacrawler.tools.command.aichat.functions.TableDecriptionFunctionParameters.TableDescriptionScope.TRIGGERS;
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
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.aichat.function.test.utility.FunctionExecutionTestUtility;
import schemacrawler.tools.command.aichat.functions.TableDecriptionFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.TableDecriptionFunctionParameters;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TableDescriptionFunctionTest {

  private Catalog catalog;

  @Test
  public void describeAllTables(final TestContext testContext) throws Exception {
    final TableDecriptionFunctionParameters args =
        new TableDecriptionFunctionParameters(null, null);
    describeTable(testContext, args, true);
  }

  @Test
  public void describeTable(final TestContext testContext) throws Exception {
    final TableDecriptionFunctionParameters args =
        new TableDecriptionFunctionParameters("AUTHORS", null);
    describeTable(testContext, args, true);
  }

  @Test
  public void describeTableColumns(final TestContext testContext) throws Exception {
    final TableDecriptionFunctionParameters args =
        new TableDecriptionFunctionParameters("ΒΙΒΛΊΑ", COLUMNS);
    describeTable(testContext, args, true);
  }

  @Test
  public void describeTableForeignKeys(final TestContext testContext) throws Exception {
    final TableDecriptionFunctionParameters args =
        new TableDecriptionFunctionParameters("BOOKAUTHORS", FOREIGN_KEYS);
    describeTable(testContext, args, true);
  }

  @Test
  public void describeTableIndexes(final TestContext testContext) throws Exception {
    final TableDecriptionFunctionParameters args =
        new TableDecriptionFunctionParameters("BOOKAUTHORS", INDEXES);
    describeTable(testContext, args, true);
  }

  @Test
  public void describeTablePrimaryKey(final TestContext testContext) throws Exception {
    final TableDecriptionFunctionParameters args =
        new TableDecriptionFunctionParameters("AUTHORS", PRIMARY_KEY);
    describeTable(testContext, args, true);
  }

  @Test
  public void describeTableTriggers(final TestContext testContext) throws Exception {
    final TableDecriptionFunctionParameters args =
        new TableDecriptionFunctionParameters("AUTHORS", TRIGGERS);
    describeTable(testContext, args, true);
  }

  @Test
  public void describeUnknownTable(final TestContext testContext) throws Exception {
    final TableDecriptionFunctionParameters args =
        new TableDecriptionFunctionParameters("NOT_A_TABLE", null);
    describeTable(testContext, args, true);
  }

  @Test
  public void describeView(final TestContext testContext) throws Exception {
    final TableDecriptionFunctionParameters args =
        new TableDecriptionFunctionParameters("AuthorsList", null);
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
    final TableDecriptionFunctionParameters args =
        new TableDecriptionFunctionParameters("AUTHORS", null);
    assertThat(
        args.toString(), is("{\"table-name\":\"AUTHORS\",\"description-scope\":\"DEFAULT\"}"));
  }

  private void describeTable(
      final TestContext testContext,
      final TableDecriptionFunctionParameters args,
      final boolean hasResults)
      throws Exception {

    final TableDecriptionFunctionDefinition functionDefinition =
        new TableDecriptionFunctionDefinition();
    FunctionExecutionTestUtility.assertFunctionExecution(
        testContext, functionDefinition, args, catalog, null, hasResults);
  }
}
