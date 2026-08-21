/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.function.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.tools.ai.functions.AboutDatabaseFunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.NoParameters;
import schemacrawler.tools.ai.utility.JsonUtility;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AboutDatabaseFunctionTest extends AbstractFunctionTest {

  @Test
  public void serverInformation(final TestContext testContext) throws Exception {
    final AboutDatabaseFunctionDefinition functionDefinition =
        new AboutDatabaseFunctionDefinition();

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final FunctionExecutor<NoParameters> executor = functionDefinition.newExecutor();
      executor.setCatalog(catalog);
      executor.setERModel(erModel);
      final FunctionReturn functionReturn = executor.call();
      final String jsonContent = functionReturn.get();
      // Format JSON content, so that the content is formatted on multiple lines
      // This prevents neutering
      final JsonNode node = JsonUtility.mapper.readTree(jsonContent);
      JsonMapper.builder().enable(SerializationFeature.INDENT_OUTPUT).build().writeValue(out, node);
    }
    final String expectedResultsResource = testContext.testMethodFullName();
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(expectedResultsResource)));
  }
}
