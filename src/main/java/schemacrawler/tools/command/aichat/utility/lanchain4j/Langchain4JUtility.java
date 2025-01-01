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

package schemacrawler.tools.command.aichat.utility.lanchain4j;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import static java.util.Objects.requireNonNull;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.model.chat.request.json.JsonEnumSchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchemaElement;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import dev.langchain4j.service.tool.ToolExecutor;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.aichat.FunctionDefinition;
import schemacrawler.tools.command.aichat.FunctionDefinition.FunctionType;
import schemacrawler.tools.command.aichat.functions.ExitFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.FunctionDefinitionRegistry;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class Langchain4JUtility {

  private static final Logger LOGGER =
      Logger.getLogger(Langchain4JUtility.class.getCanonicalName());

  public static boolean isExitCondition(final List<ChatMessage> completions) {
    requireNonNull(completions, "No completions provided");
    final String exitFunctionName = new ExitFunctionDefinition().getName();
    for (final ListIterator<ChatMessage> iterator = completions.listIterator(completions.size());
        iterator.hasPrevious(); ) {
      if (iterator.previous()
          instanceof final ToolExecutionResultMessage toolExecutionResultMessage) {
        if (toolExecutionResultMessage.toolName().equals(exitFunctionName)) {
          return true;
        }
      }
    }
    return false;
  }

  public static Map<ToolSpecification, ToolExecutor> toolsList(
      final Catalog catalog, final Connection connection) {

    final List<ToolSpecification> toolSpecifications = new ArrayList<>();
    for (final FunctionDefinition<?> functionDefinition :
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry().getFunctionDefinitions()) {
      if (functionDefinition.getFunctionType() != FunctionType.USER) {
        continue;
      }

      try {
        final Class<?> parametersClass = functionDefinition.getParametersClass();
        final Map<String, JsonNode> jsonSchema = jsonSchema(parametersClass);
        final Map<String, JsonSchemaElement> properties = toProperties(jsonSchema);
        final JsonObjectSchema parameters =
            JsonObjectSchema.builder().properties(properties).build();

        final ToolSpecification toolSpecification =
            ToolSpecification.builder()
                .name(functionDefinition.getName())
                .description(functionDefinition.getDescription())
                .parameters(parameters)
                .build();
        toolSpecifications.add(toolSpecification);
      } catch (final Exception e) {
        LOGGER.log(
            Level.WARNING,
            String.format("Could not load <%s>", functionDefinition.getFunctionName()),
            e);
      }
    }

    final ToolExecutor executor = new Langchain4JToolExecutor(catalog, connection);
    final Map<ToolSpecification, ToolExecutor> toolSpecificationsMap = new HashMap<>();
    for (final ToolSpecification toolSpecification : toolSpecifications) {
      toolSpecificationsMap.put(toolSpecification, executor);
    }

    return toolSpecificationsMap;
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

  private static Map<String, JsonSchemaElement> toProperties(
      final Map<String, JsonNode> mapJsonSchema) {
    final Map<String, JsonSchemaElement> properties = new HashMap<>();
    for (final Entry<String, JsonNode> entry : mapJsonSchema.entrySet()) {
      final String propertyName = entry.getKey();
      final JsonNode propertyNode = entry.getValue();
      final String type = propertyNode.get("type").asText();
      final String description = propertyNode.get("description").asText();
      final JsonNode enumNode = propertyNode.get("enum");
      JsonSchemaElement jsonSchemaElement;
      if ("string".equals(type)) {
        if (enumNode == null) {
          jsonSchemaElement = JsonStringSchema.builder().description(description).build();
        } else {
          final List<String> enumValues = new ArrayList<>();
          enumNode.elements().forEachRemaining(node -> enumValues.add(node.asText()));
          jsonSchemaElement =
              JsonEnumSchema.builder().description(description).enumValues(enumValues).build();
        }
        properties.put(propertyName, jsonSchemaElement);
      }
    }
    return properties;
  }

  private Langchain4JUtility() {
    // Prevent instantiation
  }
}