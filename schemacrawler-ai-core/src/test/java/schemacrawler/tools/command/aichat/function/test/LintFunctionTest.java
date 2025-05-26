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

import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
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
import schemacrawler.test.utility.WithSystemProperty;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.aichat.function.test.utility.FunctionExecutionTestUtility;
import schemacrawler.tools.command.aichat.functions.text.LintFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.text.LintFunctionParameters;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LintFunctionTest {

  private Catalog catalog;

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void lintAllTables(final TestContext testContext, final Connection connection)
      throws Exception {
    final LintFunctionParameters args = new LintFunctionParameters(null);
    lintTable(testContext, args, connection, true);
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void lintTable(final TestContext testContext, final Connection connection)
      throws Exception {
    final LintFunctionParameters args = new LintFunctionParameters("AUTHORS");
    lintTable(testContext, args, connection, true);
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void lintUnknownTable(final TestContext testContext, final Connection connection)
      throws Exception {
    final LintFunctionParameters args = new LintFunctionParameters("NOT_A_TABLE");
    lintTable(testContext, args, connection, true);
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

  private void lintTable(
      final TestContext testContext,
      final LintFunctionParameters args,
      final Connection connection,
      final boolean hasResults)
      throws Exception {

    final LintFunctionDefinition functionDefinition = new LintFunctionDefinition();
    FunctionExecutionTestUtility.assertFunctionExecution(
        testContext, functionDefinition, args, catalog, connection, hasResults);
  }
}
