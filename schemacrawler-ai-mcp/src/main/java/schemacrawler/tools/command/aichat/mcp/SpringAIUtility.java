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

package schemacrawler.tools.command.aichat.mcp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.util.json.JsonParser;
import org.springframework.lang.Nullable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaVersion;
import schemacrawler.tools.command.aichat.FunctionDefinition;
import schemacrawler.tools.command.aichat.FunctionDefinition.FunctionType;
import schemacrawler.tools.command.aichat.functions.FunctionDefinitionRegistry;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class SpringAIUtility {

  public record SpringAIToolCallback(ToolDefinition toolDefinition) implements ToolCallback {

    public SpringAIToolCallback {
      Objects.requireNonNull(toolDefinition, "Tool definition must not be null");
    }

    @Override
    public ToolDefinition getToolDefinition() {
      return toolDefinition;
    }

    @Override
    public String call(final String toolInput) {
      final String callMessage =
          String.format(
              "Call to <%s>%n%s%nTool was successfully executed with no return value.",
              toolDefinition.name(), toolInput);
      System.out.println(callMessage);
      return callMessage;
    }

    @Override
    public String call(final String toolInput, @Nullable final ToolContext tooContext) {
      return call(toolInput);
    }
  }

  private static final Logger LOGGER = Logger.getLogger(SpringAIUtility.class.getCanonicalName());

  public static List<ToolCallback> toolCallbacks(final List<ToolDefinition> tools) {
    Objects.requireNonNull(tools, "Tools must not be null");
    final List<ToolCallback> toolCallbacks = new ArrayList<>();
    for (final ToolDefinition toolDefinition : tools) {
      toolCallbacks.add(new SpringAIToolCallback(toolDefinition));
    }
    return toolCallbacks;
  }

  public static List<ToolDefinition> tools() {

    final List<ToolDefinition> toolDefinitions = new ArrayList<>();
    for (final FunctionDefinition<?> functionDefinition :
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry().getFunctionDefinitions()) {
      if (functionDefinition.getFunctionType() != FunctionType.USER) {
        continue;
      }

      try {
        final ToolDefinition toolDefinition =
            ToolDefinition.builder()
                .name(functionDefinition.getName())
                .description(functionDefinition.getDescription())
                .inputSchema(generateToolInput(functionDefinition.getParametersClass()))
                .build();
        toolDefinitions.add(toolDefinition);
      } catch (final Exception e) {
        LOGGER.log(
            Level.WARNING, String.format("Could not load <%s>", functionDefinition.getName()), e);
      }
    }

    return toolDefinitions;
  }

  /**
   * @see org.springframework.ai.util.json.schema.JsonSchemaGenerator
   */
  private static String generateToolInput(final Class<?> parametersClass) throws Exception {
    Objects.requireNonNull(parametersClass, "Parameters must not be null");

    final Map<String, JsonNode> parametersJsonSchema = jsonSchema(parametersClass);
    final ObjectNode schema = JsonParser.getObjectMapper().createObjectNode();
    schema.put("$schema", SchemaVersion.DRAFT_2020_12.getIdentifier());
    schema.put("type", "object");

    final ObjectNode properties = schema.putObject("properties");
    for (final Entry<String, JsonNode> parameter : parametersJsonSchema.entrySet()) {
      properties.set(parameter.getKey(), parameter.getValue());
    }

    return schema.toPrettyString();
  }

  private static Map<String, JsonNode> jsonSchema(final Class<?> parametersClass) throws Exception {
    final ObjectMapper mapper = new ObjectMapper();
    final JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
    final JsonSchema schema = schemaGen.generateSchema(parametersClass);
    final JsonNode schemaNode = mapper.valueToTree(schema);
    final JsonNode properties = schemaNode.get("properties");
    final Set<Entry<String, JsonNode>> namedProperties;
    if (properties == null) {
      namedProperties = new HashSet<>();
    } else {
      namedProperties = properties.properties();
    }
    final Map<String, JsonNode> propertiesMap = new HashMap<>();
    for (final Entry<String, JsonNode> entry : namedProperties) {
      propertiesMap.put(entry.getKey(), entry.getValue());
    }
    return propertiesMap;
  }

  private SpringAIUtility() {
    // Prevent instantiation
  }
}
