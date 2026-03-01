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

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.tools.ai.functions.DescribeRelationshipsFunctionDefinition;
import schemacrawler.tools.ai.functions.DescribeRelationshipsFunctionParameters;
import schemacrawler.tools.ai.tools.FunctionCallback;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import tools.jackson.databind.JsonNode;
import us.fatehi.test.utility.TestObjectUtility;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExecuteDescribeRelationshipsFunctionTest extends AbstractFunctionTest {

  @Test
  public void testExecute() throws Exception {
    final DatabaseConnectionSource connectionSource =
        DatabaseConnectionSources.fromConnection(TestObjectUtility.mockConnection());
    final DescribeRelationshipsFunctionDefinition definition =
        new DescribeRelationshipsFunctionDefinition();
    final FunctionCallback<DescribeRelationshipsFunctionParameters> callback =
        new FunctionCallback<>(definition, catalog, erModel);
    final String arguments =
        """
        {
          "cardinality" : "many_many"
        }
        """;
    final JsonFunctionReturn actualReturn =
        (JsonFunctionReturn) callback.execute(arguments, connectionSource);

    assertThat(actualReturn, is(not(nullValue())));

    final JsonNode jsonNode = actualReturn.getResult();
    assertThat(jsonNode.isArray(), is(true));

    final List<JsonNode> list = new ArrayList<>();
    jsonNode.iterator().forEachRemaining(list::add);
    assertThat(list, hasSize(1));

    final JsonNode relationshipDocument = list.get(0);
    assertThat(relationshipDocument.get("name").asString(), is("BOOKAUTHORS"));
  }
}
