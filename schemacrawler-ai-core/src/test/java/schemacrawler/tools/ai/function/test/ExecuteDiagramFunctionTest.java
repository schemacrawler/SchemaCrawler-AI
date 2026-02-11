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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.tools.ai.functions.DiagramFunctionDefinition;
import schemacrawler.tools.ai.functions.DiagramFunctionParameters;
import schemacrawler.tools.ai.tools.FunctionCallback;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import tools.jackson.databind.JsonNode;
import us.fatehi.test.utility.TestObjectUtility;
import us.fatehi.test.utility.extensions.ResolveTestContext;

@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExecuteDiagramFunctionTest extends AbstractFunctionTest {

  @Disabled
  @Test
  public void testExecute() throws Exception {
    final Connection connection = TestObjectUtility.mockConnection();
    final DiagramFunctionDefinition definition = new DiagramFunctionDefinition();
    final FunctionCallback<DiagramFunctionParameters> callback =
        new FunctionCallback<>(definition, catalog, erModel);
    final String arguments =
        """
        {
          "diagram-type" : "MERMAID",
          "include-child-tables" : true,
          "include-referenced-tables" : false,
          "table-name" : "(Authors|Books|BookAuthors)"
        }
        """;
    final JsonFunctionReturn actualReturn =
        (JsonFunctionReturn) callback.execute(arguments, connection);

    assertThat(actualReturn, is(not(nullValue())));

    final JsonNode jsonNode = actualReturn.getResult();
    assertThat(jsonNode.isArray(), is(true));

    final List<JsonNode> list = new ArrayList<>();
    jsonNode.iterator().forEachRemaining(list::add);
    assertThat(list, hasSize(1));
  }
}
