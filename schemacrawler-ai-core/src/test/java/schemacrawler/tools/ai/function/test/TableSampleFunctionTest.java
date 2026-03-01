/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.function.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.tools.ai.functions.TableSampleFunctionDefinition;
import schemacrawler.tools.ai.functions.TableSampleFunctionParameters;
import schemacrawler.tools.ai.utility.test.FunctionExecutionTestUtility;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.test.utility.extensions.WithSystemProperty;

@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TableSampleFunctionTest extends AbstractFunctionTest {

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void sampleAllTables(final TestContext testContext) throws Exception {
    final TableSampleFunctionParameters args = new TableSampleFunctionParameters(null);
    sampleTable(testContext, args, true);
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void sampleTable(final TestContext testContext) throws Exception {
    final TableSampleFunctionParameters args = new TableSampleFunctionParameters("AUTHORS");
    sampleTable(testContext, args, true);
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void sampleUnknownTable(final TestContext testContext) throws Exception {
    final TableSampleFunctionParameters args = new TableSampleFunctionParameters("NOT_A_TABLE");
    sampleTable(testContext, args, true);
  }

  private void sampleTable(
      final TestContext testContext,
      final TableSampleFunctionParameters args,
      final boolean hasResults)
      throws Exception {

    final TableSampleFunctionDefinition functionDefinition = new TableSampleFunctionDefinition();
    FunctionExecutionTestUtility.assertFunctionExecution(
        testContext, functionDefinition, args, catalog, erModel, connectionSource, hasResults);
  }
}
