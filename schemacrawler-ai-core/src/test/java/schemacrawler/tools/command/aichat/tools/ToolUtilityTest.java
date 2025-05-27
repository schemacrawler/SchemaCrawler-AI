/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.aichat.tools;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.JsonNode;

public class ToolUtilityTest {

  // Sample parameter class for testing
  public static class TestParameters implements FunctionParameters {
    private String requiredParam;
    private String optionalParam;

    public String getOptionalParam() {
      return optionalParam;
    }

    public String getRequiredParam() {
      return requiredParam;
    }

    public void setOptionalParam(String optionalParam) {
      this.optionalParam = optionalParam;
    }

    public void setRequiredParam(String requiredParam) {
      this.requiredParam = requiredParam;
    }
  }

  // Sample FunctionExecutor implementation for testing
  private abstract static class TestFunctionExecutor implements FunctionExecutor<TestParameters> {
    private final UUID executorId = UUID.randomUUID();

    @Override
    public void configure(TestParameters parameters) {
      // Do nothing for test
    }

    public void execute() {
      // Do nothing for test
    }

    @Override
    public schemacrawler.schema.Catalog getCatalog() {
      return null;
    }

    @Override
    public java.sql.Connection getConnection() {
      return null;
    }

    @Override
    public String getDescription() {
      return "Test executor";
    }

    @Override
    public UUID getExecutorInstanceId() {
      return executorId;
    }

    @Override
    public void initialize() {
      // Do nothing for test
    }

    @Override
    public void setConnection(java.sql.Connection connection) {
      // Do nothing for test
    }
  }

  @Test
  public void testExtractParametersSchema() {
    final Map<String, JsonNode> schema = ToolUtility.extractParametersSchema(TestParameters.class);

    assertThat(schema, notNullValue());
    assertThat(schema.isEmpty(), is(false));
    assertThat(schema.size(), greaterThan(0));
    assertThat(schema, hasKey("requiredParam"));

    final JsonNode requiredParamSchema = schema.get("requiredParam");
    assertThat(requiredParamSchema, notNullValue());
    assertThat(requiredParamSchema.has("type"), is(true));

    if (schema.containsKey("optionalParam")) {
      final JsonNode optionalParamSchema = schema.get("optionalParam");
      assertThat(optionalParamSchema, notNullValue());
      assertThat(optionalParamSchema.has("type"), is(true));
    }
  }

  @Test
  public void testToToolSpecification() {
    // Create a mock FunctionDefinition
    @SuppressWarnings("unchecked")
    final FunctionDefinition<TestParameters> mockFunctionDefinition =
        mock(FunctionDefinition.class);
    when(mockFunctionDefinition.getName()).thenReturn("test-function");
    when(mockFunctionDefinition.getDescription()).thenReturn("Test function description");
    when(mockFunctionDefinition.getParametersClass()).thenReturn(TestParameters.class);

    final ToolSpecification toolSpecification =
        ToolUtility.toToolSpecification(mockFunctionDefinition);

    assertThat(toolSpecification, notNullValue());
    assertThat(toolSpecification.name(), is("test-function"));
    assertThat(toolSpecification.description(), is("Test function description"));
    assertThat(toolSpecification.parameters(), notNullValue());

    final String parametersString = toolSpecification.getParametersAsString();
    assertThat(parametersString, containsString("type"));
    assertThat(parametersString, containsString("object"));
    assertThat(parametersString, containsString("properties"));
    assertThat(parametersString, containsString("requiredParam"));
  }
}
