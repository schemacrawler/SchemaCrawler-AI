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
import schemacrawler.importance.model.implementation.ImportanceModelBuilder;
import schemacrawler.tools.ai.functions.TableImportanceFunctionDefinition;
import schemacrawler.tools.ai.functions.TableImportanceFunctionParameters;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import tools.jackson.databind.JsonNode;

public class TableImportanceFunctionTest extends AbstractFunctionTest {

  @Test
  public void parameterDefaults() {
    assertThat(new TableImportanceFunctionParameters().maxImportantTables(), is(5));
    assertThat(new TableImportanceFunctionParameters("", null).maxImportantTables(), is(5));
  }

  @Test
  public void reportAllTablesAndViews() throws Exception {
    final JsonNode importanceWithZeroLimit =
        execute(new TableImportanceFunctionParameters("", -1)).getResult().get("importance");
    final JsonNode importanceWithNegativeLimit =
        execute(new TableImportanceFunctionParameters("", -1)).getResult().get("importance");

    assertThat(importanceWithZeroLimit.size(), greaterThan(0));
    assertThat(importanceWithNegativeLimit.size(), is(importanceWithZeroLimit.size()));
    for (int i = 1; i < importanceWithZeroLimit.size(); i++) {
      final JsonNode previous = importanceWithZeroLimit.get(i - 1);
      final JsonNode current = importanceWithZeroLimit.get(i);
      final double previousScore =
          previous.get("tableImportance").get("importanceScore").asDouble();
      final double currentScore = current.get("tableImportance").get("importanceScore").asDouble();
      assertThat(previousScore >= currentScore, is(true));
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

  @Test
  public void reportDefaultMaxImportantTables() throws Exception {
    final JsonNode importance =
        execute(new TableImportanceFunctionParameters()).getResult().get("importance");

    assertThat(importance.size(), is(5));
  }

  @Test
  public void reportContainsOnlyImportanceEntries() throws Exception {
    final JsonNode result = execute(new TableImportanceFunctionParameters()).getResult();

    assertThat(result.size(), is(1));
    assertThat(result.has("importance"), is(true));
    assertThat(result.has("communities"), is(false));
  }

  private JsonFunctionReturn execute(final TableImportanceFunctionParameters parameters)
      throws Exception {
    final FunctionExecutor<TableImportanceFunctionParameters> executor =
        new TableImportanceFunctionDefinition().newExecutor();
    executor.configure(parameters);
    executor.setCatalog(catalog);
    executor.setERModel(erModel);
    executor.setImportanceModel(ImportanceModelBuilder.builder(catalog).build());
    return (JsonFunctionReturn) executor.call();
  }
}
