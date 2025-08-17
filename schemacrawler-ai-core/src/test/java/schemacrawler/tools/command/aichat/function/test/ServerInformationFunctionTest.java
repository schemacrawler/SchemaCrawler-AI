/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.function.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.ai.functions.ServerInformationFunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.NoParameters;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServerInformationFunctionTest extends AbstractFunctionTest {

  @Test
  public void serverInformation(final TestContext testContext) throws Exception {
    final ServerInformationFunctionDefinition functionDefinition =
        new ServerInformationFunctionDefinition();

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final FunctionExecutor<NoParameters> executor = functionDefinition.newExecutor();
      executor.setCatalog(catalog);
      final FunctionReturn functionReturn = executor.call();
      out.write(functionReturn.get());
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
