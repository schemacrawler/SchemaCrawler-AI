/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.function.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.tools.ai.functions.DiagramFunctionDefinition;
import schemacrawler.tools.ai.functions.DiagramFunctionParameters;
import schemacrawler.tools.ai.utility.test.FunctionExecutionTestUtility;
import us.fatehi.mcp_json_schema.utility.DeserializationUtility;
import us.fatehi.test.utility.TestObjectUtility;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExecuteDiagramFunctionTest extends AbstractFunctionTest {

  @Test
  public void testExecute(final TestContext testContext) throws Exception {
    DatabaseConnectionSource connectionSource =
        DatabaseConnectionSources.fromConnection(TestObjectUtility.mockConnection());
    final DiagramFunctionDefinition functionDefinition = new DiagramFunctionDefinition();
    final String argumentsString =
        """
        {
          "table_name" : "(Authors|Books|BookAuthors)",
          "include_child_tables" : true,
          "include_referenced_tables" : false,
          "diagram_type" : "MERMAID"
        }
        """;
    final DiagramFunctionParameters args =
        DeserializationUtility.instantiateArguments(
            argumentsString, DiagramFunctionParameters.class);
    assertThat("Could not instantiate arguments", args, is(not(nullValue())));

    FunctionExecutionTestUtility.assertFunctionExecution(
        testContext, functionDefinition, args, catalog, erModel, connectionSource, true);
  }
}
