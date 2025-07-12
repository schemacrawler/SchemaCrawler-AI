/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */



package schemacrawler.tools.command.aichat.function.test.utility;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import java.io.IOException;
import java.sql.Connection;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schema.Catalog;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.FunctionReturn;

public class FunctionExecutionTestUtility {

  public static <P extends FunctionParameters> void assertFunctionExecution(
      final TestContext testContext,
      final FunctionDefinition<P> functionDefinition,
      final P args,
      final Catalog catalog,
      final Connection connection,
      final boolean hasResults)
      throws Exception, IOException {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final FunctionExecutor<P> executor = functionDefinition.newExecutor();
      executor.configure(args);
      executor.setCatalog(catalog);
      if (connection != null && executor.usesConnection()) {
        executor.setConnection(connection);
      }
      final FunctionReturn functionReturn = executor.call();
      final String results = functionReturn.get();
      if (!hasResults && isBlank(results)) {
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
