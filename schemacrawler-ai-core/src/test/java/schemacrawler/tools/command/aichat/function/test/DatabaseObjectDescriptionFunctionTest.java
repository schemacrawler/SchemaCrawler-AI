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
import static schemacrawler.tools.command.aichat.functions.text.DatabaseObjectDescriptionFunctionParameters.DatabaseObjectsScope.ROUTINES;
import static schemacrawler.tools.command.aichat.functions.text.DatabaseObjectDescriptionFunctionParameters.DatabaseObjectsScope.SEQUENCES;
import static schemacrawler.tools.command.aichat.functions.text.DatabaseObjectDescriptionFunctionParameters.DatabaseObjectsScope.SYNONYMS;
import static schemacrawler.tools.command.aichat.functions.text.DatabaseObjectDescriptionFunctionParameters.DatabaseObjectsScope.TABLES;
import java.sql.Connection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.inclusionrule.IncludeAll;
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
import schemacrawler.tools.command.aichat.functions.text.DatabaseObjectDescriptionFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.text.DatabaseObjectDescriptionFunctionParameters;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseObjectDescriptionFunctionTest {

  private Catalog catalog;

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

  @BeforeAll
  public void loadCatalog(final Connection connection) throws Exception {

    final SchemaRetrievalOptions schemaRetrievalOptions = TestUtility.newSchemaRetrievalOptions();

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"))
            .includeTables(new IncludeAll())
            .includeAllRoutines()
            .includeAllSynonyms()
            .includeAllSequences();
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
    final DatabaseObjectDescriptionFunctionParameters args =
        new DatabaseObjectDescriptionFunctionParameters(null, ROUTINES);
    assertThat(
        args.toString(),
        is("{\"database-object-name\":\"\",\"database-objects-scope\":\"ROUTINES\"}"));
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
