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
import static schemacrawler.tools.ai.model.DatabaseObjectType.SEQUENCES;
import static schemacrawler.tools.ai.model.DatabaseObjectType.TABLES;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.command.aichat.functions.text.DatabaseObjectListFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.text.DatabaseObjectListFunctionParameters;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RefilterTest extends AbstractFunctionTest {

  @Test
  public void refilterTest(final TestContext testContext) throws Exception {
    final DatabaseObjectListFunctionParameters args1 =
        new DatabaseObjectListFunctionParameters(SEQUENCES);
    databaseObjects(testContext.testMethodFullName() + ".sequences", args1);

    final DatabaseObjectListFunctionParameters args2 =
        new DatabaseObjectListFunctionParameters(TABLES);
    databaseObjects(testContext.testMethodFullName() + ".tables", args2);
  }

  private void databaseObjects(
      final String reference, final DatabaseObjectListFunctionParameters args) throws Exception {

    final DatabaseObjectListFunctionDefinition functionDefinition =
        new DatabaseObjectListFunctionDefinition();

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final FunctionExecutor<DatabaseObjectListFunctionParameters> executor =
          functionDefinition.newExecutor();
      executor.configure(args);
      executor.setCatalog(catalog);
      final FunctionReturn functionReturn = executor.call();
      out.write(functionReturn.get());
    }
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(reference)));
  }
}
