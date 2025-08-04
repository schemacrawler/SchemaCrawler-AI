/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.function.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.schema.Catalog;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.command.aichat.functions.text.ExitFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.text.NoParameters;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExitFunctionTest {

  @Test
  public void exit(final TestContext testContext) throws Exception {

    final ExitFunctionDefinition functionDefinition = new ExitFunctionDefinition();
    final NoParameters args = new NoParameters();

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final FunctionExecutor<NoParameters> executor = functionDefinition.newExecutor();
      executor.configure(args);
      executor.setCatalog(mock(Catalog.class));
      final FunctionReturn functionReturn = executor.call();
      out.write(functionReturn.get());
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
