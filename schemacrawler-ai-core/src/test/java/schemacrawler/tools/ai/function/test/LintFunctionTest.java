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
import schemacrawler.tools.ai.functions.LintFunctionDefinition;
import schemacrawler.tools.ai.functions.LintFunctionParameters;
import schemacrawler.tools.ai.utility.test.FunctionExecutionTestUtility;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.test.utility.extensions.WithSystemProperty;

@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LintFunctionTest extends AbstractFunctionTest {

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void lintAllTables(final TestContext testContext) throws Exception {
    final LintFunctionParameters args = new LintFunctionParameters(null);
    lintTable(testContext, args, true);
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void lintTable(final TestContext testContext) throws Exception {
    final LintFunctionParameters args = new LintFunctionParameters("AUTHORS");
    lintTable(testContext, args, true);
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void lintUnknownTable(final TestContext testContext) throws Exception {
    final LintFunctionParameters args = new LintFunctionParameters("NOT_A_TABLE");
    lintTable(testContext, args, true);
  }

  private void lintTable(
      final TestContext testContext, final LintFunctionParameters args, final boolean hasResults)
      throws Exception {

    final LintFunctionDefinition functionDefinition = new LintFunctionDefinition();
    FunctionExecutionTestUtility.assertFunctionExecution(
        testContext, functionDefinition, args, catalog, erModel, connectionSource, hasResults);
  }
}
