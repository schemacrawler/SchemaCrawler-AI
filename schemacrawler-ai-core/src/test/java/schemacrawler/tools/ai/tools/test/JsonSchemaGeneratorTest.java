/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.tools.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.ai.functions.DescribeRoutinesFunctionParameters;
import schemacrawler.tools.ai.functions.DescribeTablesFunctionParameters;
import schemacrawler.tools.ai.functions.DiagramFunctionParameters;
import schemacrawler.tools.ai.functions.LintFunctionParameters;
import schemacrawler.tools.ai.functions.ListAcrossTablesFunctionParameters;
import schemacrawler.tools.ai.functions.ListFunctionParameters;
import schemacrawler.tools.ai.functions.TableSampleFunctionParameters;
import schemacrawler.tools.ai.tools.FunctionParameters;
import tools.jackson.databind.JsonNode;
import us.fatehi.mcp_json_schema.McpJsonSchemaUtility;
import us.fatehi.test.utility.TestWriter;

public class JsonSchemaGeneratorTest {

  @Test
  public void functionParameters() {

    final List<Class<? extends FunctionParameters>> functionParameters =
        List.of(
            DescribeTablesFunctionParameters.class,
            DescribeRoutinesFunctionParameters.class,
            LintFunctionParameters.class,
            ListFunctionParameters.class,
            ListAcrossTablesFunctionParameters.class,
            DiagramFunctionParameters.class,
            TableSampleFunctionParameters.class);

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      for (final Class<?> parametersClass : functionParameters) {
        final JsonNode schemaNode = McpJsonSchemaUtility.generateJsonSchema(parametersClass);
        // final JsonNode schemaNode =
        // ToolUtility.extractParametersSchemaNode(parametersClass);

        out.println(parametersClass.getSimpleName());
        out.println(schemaNode.toPrettyString().indent(2));
        out.println();
        out.println();
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource("parameters-json-schemas.txt")));
  }
}
