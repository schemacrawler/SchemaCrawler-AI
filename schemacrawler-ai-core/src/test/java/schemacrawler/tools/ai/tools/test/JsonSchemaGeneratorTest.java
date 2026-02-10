/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.tools.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.util.Collection;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import schemacrawler.schema.FunctionParameter;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionDefinitionRegistry;
import tools.jackson.databind.JsonNode;
import us.fatehi.mcp_json_schema.McpJsonSchemaUtility;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
public class JsonSchemaGeneratorTest {

  private static final int NUM_FUNCTIONS = 8;

  private static Stream<FunctionDefinition<?>> functionDefinitionsProvider() {
    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
    final Collection<FunctionDefinition<?>> functionDefinitions = registry.getFunctionDefinitions();
    assertThat(functionDefinitions, hasSize(NUM_FUNCTIONS));
    return functionDefinitions.stream();
  }

  @Disabled
  @ParameterizedTest
  @MethodSource("functionDefinitionsProvider")
  void functionParametersPerDefinition(
      final FunctionDefinition<?> functionDefinition, final TestContext testContext)
      throws Exception {

    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
    final Collection<FunctionDefinition<?>> functionDefinitions = registry.getFunctionDefinitions();
    assertThat(functionDefinitions, hasSize(NUM_FUNCTIONS));

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Class<? extends FunctionParameter> parametersClass =
          (Class<? extends FunctionParameter>) functionDefinition.getParametersClass();
      final JsonNode schemaNode = McpJsonSchemaUtility.generateJsonSchema(parametersClass);

      out.println(parametersClass.getSimpleName());
      out.println(schemaNode.toPrettyString().indent(2));
      out.println();
      out.println();
    }
    final String referenceFile =
        "%s-%s.txt".formatted(testContext.testMethodFullName(), functionDefinition.getName());
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(referenceFile)));
  }
}
