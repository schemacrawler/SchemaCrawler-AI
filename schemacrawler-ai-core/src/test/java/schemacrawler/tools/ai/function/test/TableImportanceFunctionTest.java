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
import static org.hamcrest.Matchers.greaterThan;

import org.junit.jupiter.api.Test;
import schemacrawler.importance.util.SchemaGraphModelBuilder;
import schemacrawler.tools.ai.functions.TableImportanceFunctionDefinition;
import schemacrawler.tools.ai.functions.TableImportanceFunctionParameters;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import tools.jackson.databind.JsonNode;

public class TableImportanceFunctionTest extends AbstractFunctionTest {

  @Test
  public void reportAllTablesAndViews() throws Exception {
    final JsonNode importance =
        execute(new TableImportanceFunctionParameters()).getResult().get("importance");

    assertThat(importance.size(), greaterThan(0));
    for (int i = 1; i < importance.size(); i++) {
      final JsonNode previous = importance.get(i - 1);
      final JsonNode current = importance.get(i);
      final double previousCentrality =
          previous.get("graphMetrics").get("betweennessCentrality").asDouble();
      final double currentCentrality =
          current.get("graphMetrics").get("betweennessCentrality").asDouble();
      assertThat(previousCentrality >= currentCentrality, is(true));
    }
  }

  @Test
  public void reportFilteredTablesAndViews() throws Exception {
    final JsonNode importance =
        execute(new TableImportanceFunctionParameters("PUBLIC\\.BOOKS\\.AUTHORS"))
            .getResult()
            .get("importance");

    assertThat(importance.size(), is(1));
    assertThat(importance.get(0).get("tableFullName").asString(), is("PUBLIC.BOOKS.AUTHORS"));
  }

  private JsonFunctionReturn execute(final TableImportanceFunctionParameters parameters)
      throws Exception {
    final FunctionExecutor<TableImportanceFunctionParameters> executor =
        new TableImportanceFunctionDefinition().newExecutor();
    executor.configure(parameters);
    executor.setCatalog(catalog);
    executor.setERModel(erModel);
    executor.setSchemaGraphModel(SchemaGraphModelBuilder.builder(catalog).build());
    return (JsonFunctionReturn) executor.call();
  }
}
