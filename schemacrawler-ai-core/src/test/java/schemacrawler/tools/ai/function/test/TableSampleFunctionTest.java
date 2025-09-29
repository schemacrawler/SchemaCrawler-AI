/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.function.test;

import java.sql.Connection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.WithSystemProperty;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.ai.functions.TableSampleFunctionDefinition;
import schemacrawler.tools.ai.functions.TableSampleFunctionParameters;
import schemacrawler.tools.ai.utility.test.FunctionExecutionTestUtility;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TableSampleFunctionTest extends AbstractFunctionTest {

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void sampleAllTables(final TestContext testContext, final Connection connection)
      throws Exception {
    final TableSampleFunctionParameters args = new TableSampleFunctionParameters(null);
    sampleTable(testContext, args, connection, true);
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void sampleTable(final TestContext testContext, final Connection connection)
      throws Exception {
    final TableSampleFunctionParameters args = new TableSampleFunctionParameters("AUTHORS");
    sampleTable(testContext, args, connection, true);
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void sampleUnknownTable(final TestContext testContext, final Connection connection)
      throws Exception {
    final TableSampleFunctionParameters args = new TableSampleFunctionParameters("NOT_A_TABLE");
    sampleTable(testContext, args, connection, true);
  }

  private void sampleTable(
      final TestContext testContext,
      final TableSampleFunctionParameters args,
      final Connection connection,
      final boolean hasResults)
      throws Exception {

    final TableSampleFunctionDefinition functionDefinition = new TableSampleFunctionDefinition();
    FunctionExecutionTestUtility.assertFunctionExecution(
        testContext, functionDefinition, args, catalog, connection, hasResults);
  }
}
