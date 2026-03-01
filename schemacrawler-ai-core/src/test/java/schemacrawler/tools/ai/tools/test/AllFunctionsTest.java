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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.util.Collection;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.schema.Catalog;
import schemacrawler.test.utility.crawl.LightCatalogUtility;
import schemacrawler.tools.ai.tools.FunctionCallback;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionDefinitionRegistry;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import tools.jackson.databind.JsonNode;
import us.fatehi.test.utility.TestObjectUtility;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

@ResolveTestContext
public class AllFunctionsTest {

  private static final int NUM_FUNCTIONS = 8;

  private static Stream<FunctionDefinition<?>> functionDefinitionsProvider() {
    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
    final Collection<FunctionDefinition<?>> functionDefinitions = registry.getFunctionDefinitions();
    assertThat(functionDefinitions, hasSize(NUM_FUNCTIONS));
    return functionDefinitions.stream();
  }

  private DatabaseConnectionSource connectionSource;
  private Catalog catalog;
  private ERModel erModel;

  @BeforeEach
  public void setupCatalog() {
    connectionSource = DatabaseConnectionSources.fromConnection(TestObjectUtility.mockConnection());
    catalog = LightCatalogUtility.lightCatalog();
    erModel = TestObjectUtility.makeTestObject(ERModel.class);
  }

  @ParameterizedTest
  @MethodSource("functionDefinitionsProvider")
  public void testExecute(final FunctionDefinition<?> functionDefinition) throws Exception {
    final FunctionCallback<?> callback =
        new FunctionCallback<>(functionDefinition, catalog, erModel);
    final FunctionReturn actualReturn =
        switch (functionDefinition.getName()) {
          case "diagram" -> new JsonFunctionReturn();
          default ->
              assertDoesNotThrow(
                  () -> callback.execute(null, connectionSource), functionDefinition.getName());
        };
    assertThat(actualReturn, is(not(nullValue())));
  }

  @ParameterizedTest
  @MethodSource("functionDefinitionsProvider")
  public void testInstantiateInvalidArguments(final FunctionDefinition<?> functionDefinition)
      throws Exception {
    final FunctionCallback<?> callback =
        new FunctionCallback<>(functionDefinition, catalog, erModel);
    switch (functionDefinition.getName()) {
      case "diagram":
        break;
      default:
        assertDoesNotThrow(
            () -> callback.execute("invalid-json", connectionSource), functionDefinition.getName());
    }
  }

  @ParameterizedTest
  @MethodSource("functionDefinitionsProvider")
  void functionDefinitionJson(
      final FunctionDefinition<?> functionDefinition, final TestContext testContext)
      throws Exception {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final JsonNode definitionNode = functionDefinition.toJson();
      out.println(definitionNode.toPrettyString());
    }
    final String referenceFile =
        "%s-%s.txt".formatted(testContext.testMethodFullName(), functionDefinition.getName());
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(referenceFile)));
  }
}
