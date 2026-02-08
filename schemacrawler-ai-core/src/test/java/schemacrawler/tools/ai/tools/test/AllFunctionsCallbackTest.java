/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.tools.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.sql.Connection;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.schema.Catalog;
import schemacrawler.test.utility.crawl.LightCatalogUtility;
import schemacrawler.tools.ai.tools.FunctionCallback;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionDefinitionRegistry;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import us.fatehi.test.utility.TestObjectUtility;

public class AllFunctionsCallbackTest {

  private Connection connection;
  private Catalog catalog;
  private ERModel erModel;

  @BeforeEach
  public void setupCatalog() {
    connection = TestObjectUtility.mockConnection();
    catalog = LightCatalogUtility.lightCatalog();
    erModel = TestObjectUtility.makeTestObject(ERModel.class);
  }

  @Test
  public void testExecute() throws Exception {
    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
    final Collection<FunctionDefinition<?>> functionDefinitions = registry.getFunctionDefinitions();
    for (final FunctionDefinition<?> definition : functionDefinitions) {
      final FunctionCallback<?> callback = new FunctionCallback<>(definition, catalog, erModel);
      final FunctionReturn actualReturn =
          switch (definition.getName()) {
            case "diagram" -> new JsonFunctionReturn();
            default ->
                assertDoesNotThrow(() -> callback.execute(null, connection), definition.getName());
          };
      assertThat(actualReturn, is(not(nullValue())));
    }
  }

  @Test
  public void testInstantiateInvalidArguments() throws Exception {
    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
    final Collection<FunctionDefinition<?>> functionDefinitions = registry.getFunctionDefinitions();
    for (final FunctionDefinition<?> definition : functionDefinitions) {
      final FunctionCallback<?> callback = new FunctionCallback<>(definition, catalog, erModel);
      switch (definition.getName()) {
        case "diagram":
          break;
        default:
          assertDoesNotThrow(
              () -> callback.execute("invalid-json", connection), definition.getName());
      }
    }
  }
}
