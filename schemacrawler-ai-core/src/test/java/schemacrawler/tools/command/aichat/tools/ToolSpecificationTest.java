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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ToolSpecificationTest {

  private static final String TEST_NAME = "test-tool";
  private static final String TEST_DESCRIPTION = "Test tool description";

  private ObjectMapper objectMapper;
  private ObjectNode parameters;
  private ToolSpecification toolSpecification;

  @BeforeEach
  public void setUp() {
    objectMapper = new ObjectMapper();
    parameters = objectMapper.createObjectNode();
    parameters.put("type", "object");

    ObjectNode properties = parameters.putObject("properties");
    ObjectNode paramProperty = properties.putObject("param1");
    paramProperty.put("type", "string");
    paramProperty.put("description", "Test parameter");

    toolSpecification = new ToolSpecification(TEST_NAME, TEST_DESCRIPTION, parameters);
  }

  @Test
  public void testConstructor() {
    assertThat(toolSpecification, notNullValue());
    assertThat(toolSpecification.name(), is(TEST_NAME));
    assertThat(toolSpecification.description(), is(TEST_DESCRIPTION));
    assertThat(toolSpecification.parameters(), is(parameters));
  }

  @Test
  public void testGetParametersAsString() {
    final String parametersString = toolSpecification.getParametersAsString();

    assertThat(parametersString, notNullValue());
    assertThat(parametersString.isEmpty(), is(false));
    assertThat(parametersString, containsString("type"));
    assertThat(parametersString, containsString("object"));
    assertThat(parametersString, containsString("properties"));
    assertThat(parametersString, containsString("param1"));
  }

  @Test
  public void testGetToolSpecification() {
    final JsonNode toolSpec = toolSpecification.getToolSpecification();

    assertThat(toolSpec, notNullValue());
    assertThat(toolSpec.isObject(), is(true));
    assertThat(toolSpec.has("name"), is(true));
    assertThat(toolSpec.get("name").asText(), is(TEST_NAME));
    assertThat(toolSpec.has("description"), is(true));
    assertThat(toolSpec.get("description").asText(), is(TEST_DESCRIPTION));
    assertThat(toolSpec.has("parameters"), is(true));
    assertThat(toolSpec.get("parameters"), is(parameters));
  }

  @Test
  public void testToString() {
    final String toolSpecString = toolSpecification.toString();

    assertThat(toolSpecString, notNullValue());
    assertThat(toolSpecString.isEmpty(), is(false));
    assertThat(toolSpecString, containsString(TEST_NAME));
    assertThat(toolSpecString, containsString(TEST_DESCRIPTION));
    assertThat(toolSpecString, containsString("parameters"));
    assertThat(toolSpecString, containsString("param1"));
  }
}
