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

package schemacrawler.tools.command.chatgpt.functions.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectDescriptionFunctionDefinition.DatabaseObjectsScope.ROUTINES;
import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectDescriptionFunctionDefinition.DatabaseObjectsScope.SEQUENCES;
import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectDescriptionFunctionDefinition.DatabaseObjectsScope.SYNONYMS;
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
import schemacrawler.tools.command.chatgpt.FunctionReturn;
import schemacrawler.tools.command.chatgpt.functions.DatabaseObjectDescriptionFunctionDefinition;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseObjectDescriptionFunctionTest {

  private Catalog catalog;

  @Test
  public void describeAllRoutines(final TestContext testContext) throws Exception {
    final DatabaseObjectDescriptionFunctionDefinition functionDefinition =
        new DatabaseObjectDescriptionFunctionDefinition();
    functionDefinition.setDatabaseObjectsScope(ROUTINES);
    describeDatabaseObject(testContext, functionDefinition);
  }

  @Test
  public void describeNone(final TestContext testContext) throws Exception {
    final DatabaseObjectDescriptionFunctionDefinition functionDefinition =
        new DatabaseObjectDescriptionFunctionDefinition();
    describeDatabaseObject(testContext, functionDefinition);
  }

  @Test
  public void describeRoutines(final TestContext testContext) throws Exception {
    final DatabaseObjectDescriptionFunctionDefinition functionDefinition =
        new DatabaseObjectDescriptionFunctionDefinition();
    functionDefinition.setDatabaseObjectsScope(ROUTINES);
    functionDefinition.setDatabaseObjectName("CUSTOMADD");
    describeDatabaseObject(testContext, functionDefinition);
  }

  @Test
  public void describeSequences(final TestContext testContext) throws Exception {
    final DatabaseObjectDescriptionFunctionDefinition functionDefinition =
        new DatabaseObjectDescriptionFunctionDefinition();
    functionDefinition.setDatabaseObjectsScope(SEQUENCES);
    functionDefinition.setDatabaseObjectName("PUBLISHER_ID_SEQ");
    describeDatabaseObject(testContext, functionDefinition);
  }

  @Test
  public void describeSynonyms(final TestContext testContext) throws Exception {
    final DatabaseObjectDescriptionFunctionDefinition functionDefinition =
        new DatabaseObjectDescriptionFunctionDefinition();
    functionDefinition.setDatabaseObjectsScope(SYNONYMS);
    functionDefinition.setDatabaseObjectName("PUBLICATIONS");
    describeDatabaseObject(testContext, functionDefinition);
  }

  @Test
  public void describeUnknownDatabaseObject(final TestContext testContext) throws Exception {
    final DatabaseObjectDescriptionFunctionDefinition functionDefinition =
        new DatabaseObjectDescriptionFunctionDefinition();
    functionDefinition.setDatabaseObjectsScope(SYNONYMS);
    functionDefinition.setDatabaseObjectName("NOT_A SYNONYM");
    describeDatabaseObject(testContext, functionDefinition);
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
    final DatabaseObjectDescriptionFunctionDefinition functionDefinition =
        new DatabaseObjectDescriptionFunctionDefinition();
    functionDefinition.setDatabaseObjectsScope(ROUTINES);
    assertThat(
        functionDefinition.toString(),
        is("{\"database-object-name\":null,\"database-objects-scope\":\"ROUTINES\"}"));
  }

  private void describeDatabaseObject(
      final TestContext testContext,
      final DatabaseObjectDescriptionFunctionDefinition functionDefinition)
      throws Exception {

    functionDefinition.setCatalog(catalog);

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final FunctionReturn functionReturn = functionDefinition.getExecutor().get();
      out.write(functionReturn.get());
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
