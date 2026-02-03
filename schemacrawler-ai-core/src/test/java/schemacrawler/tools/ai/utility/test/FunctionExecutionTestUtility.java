/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.utility.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.sql.Connection;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.FunctionReturn;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.TestContext;

public class FunctionExecutionTestUtility {

  public static <P extends FunctionParameters> void assertFunctionExecution(
      final TestContext testContext,
      final FunctionDefinition<P> functionDefinition,
      final P args,
      final Catalog catalog,
      final ERModel erModel,
      final Connection connection,
      final boolean hasResults)
      throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final FunctionExecutor<P> executor = functionDefinition.newExecutor();
      executor.configure(args);
      executor.setCatalog(catalog);
      executor.setERModel(erModel);
      if (connection != null && executor.usesConnection()) {
        executor.setConnection(connection);
      }
      final FunctionReturn functionReturn = executor.call();
      final String results = functionReturn.get();
      if (!hasResults && results.isBlank()) {
        return;
      }
      out.write(results);
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  private FunctionExecutionTestUtility() {
    // Prevent instantiation
  }
}
