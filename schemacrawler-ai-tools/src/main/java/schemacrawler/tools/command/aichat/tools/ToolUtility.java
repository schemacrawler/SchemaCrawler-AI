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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public final class ToolUtility {

  private static final Logger LOGGER = Logger.getLogger(ToolUtility.class.getCanonicalName());
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static Map<String, JsonNode> extractParametersSchema(final Class<?> parametersClass) {
    try {
      final JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(OBJECT_MAPPER);
      final JsonSchema schema = schemaGen.generateSchema(parametersClass);
      final JsonNode schemaNode = OBJECT_MAPPER.valueToTree(schema);

      return Optional.ofNullable(schemaNode.get("properties"))
          .map(JsonNode::properties)
          .map(
              properties ->
                  properties.stream()
                      .collect(
                          Collectors.toMap(
                              Entry::getKey,
                              Entry::getValue,
                              (node1, node2) -> node1,
                              HashMap::new)))
          .orElseGet(HashMap::new);
    } catch (final JsonMappingException e) {
      LOGGER.log(
          Level.WARNING,
          String.format("Could create JSON schema for <%s>", parametersClass.getName()),
          e);
      return new HashMap<>();
    }
  }

  public static ToolSpecification toToolSpecification(
      final FunctionDefinition<?> functionDefinition) {
    Objects.requireNonNull(functionDefinition, "Function definition must not be null");
    final String functionName = functionDefinition.getName();
    final String functionDescription = functionDefinition.getDescription();
    final ObjectNode parameters = generateParametersSchema(functionDefinition.getParametersClass());
    final ToolSpecification toolSpecification =
        new ToolSpecification(functionName, functionDescription, parameters);
    LOGGER.log(
        Level.INFO,
        String.format(
            "Generated tool specification for <%s>%n%s",
            toolSpecification.name(), toolSpecification));
    return toolSpecification;
  }

  private static ObjectNode generateParametersSchema(final Class<?> parametersClass) {
    Objects.requireNonNull(parametersClass, "Parameters must not be null");

    final Map<String, JsonNode> parametersJsonSchema = extractParametersSchema(parametersClass);
    final ObjectNode schema = OBJECT_MAPPER.createObjectNode();
    // schema.set("$schema", SchemaVersion.DRAFT_2020_12.getIdentifier());
    schema.put("type", "object");

    final List<String> required = new ArrayList<>();
    final ObjectNode properties = schema.putObject("properties");
    for (final Entry<String, JsonNode> parameter : parametersJsonSchema.entrySet()) {
      final String parameterName = parameter.getKey();
      final JsonNode parameterSchema = parameter.getValue();
      if (parameterSchema.has("required") && parameterSchema.get("required").asBoolean()) {
        ((ObjectNode) parameterSchema).remove("required");
        required.add(parameterName);
      }
      properties.set(parameterName, parameterSchema);
    }
    final ArrayNode requiredArray = schema.putArray("required");
    required.forEach(requiredArray::add);

    schema.put("additionalProperties", false);

    return schema;
  }

  private ToolUtility() {
    // Prevent instantiation
  }
}
