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
