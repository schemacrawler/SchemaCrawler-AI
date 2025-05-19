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

package schemacrawler.tools.command.aichat.langchain4j;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonEnumSchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchemaElement;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import dev.langchain4j.service.tool.ToolExecutor;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.aichat.tools.FunctionDefinition;
import schemacrawler.tools.command.aichat.tools.FunctionDefinitionRegistry;
import schemacrawler.tools.command.aichat.tools.FunctionDefinition.FunctionType;
import us.fatehi.utility.UtilityMarker;

import static schemacrawler.tools.command.aichat.tools.ToolUtility.extractParametersSchema;

@UtilityMarker
public class Langchain4JUtility {

  private static final Logger LOGGER =
      Logger.getLogger(Langchain4JUtility.class.getCanonicalName());

  public static Map<String, ToolExecutor> toolExecutors(
      final Catalog catalog, final Connection connection) {

    final Map<String, ToolExecutor> toolSpecificationsMap = new HashMap<>();
    for (final FunctionDefinition<?> functionDefinition :
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry().getFunctionDefinitions()) {
      if (functionDefinition.getFunctionType() != FunctionType.USER) {
        continue;
      }
      final String functionName = functionDefinition.getName();

      final ToolExecutor executor = new Langchain4JToolExecutor(functionName, catalog, connection);
      toolSpecificationsMap.put(functionName, executor);
    }

    return toolSpecificationsMap;
  }

  public static List<ToolSpecification> tools() {

    final List<ToolSpecification> toolSpecifications = new ArrayList<>();
    for (final FunctionDefinition<?> functionDefinition :
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry().getFunctionDefinitions()) {
      if (functionDefinition.getFunctionType() != FunctionType.USER) {
        continue;
      }

      try {
        final Class<?> parametersClass = functionDefinition.getParametersClass();
        final Map<String, JsonNode> parametersSchema = extractParametersSchema(parametersClass);
        final Map<String, JsonSchemaElement> properties = toProperties(parametersSchema);
        final JsonObjectSchema parameters =
            JsonObjectSchema.builder().addProperties(properties).build();

        final ToolSpecification toolSpecification =
            ToolSpecification.builder()
                .name(functionDefinition.getName())
                .description(functionDefinition.getDescription())
                .parameters(parameters)
                .build();
        toolSpecifications.add(toolSpecification);
      } catch (final Exception e) {
        LOGGER.log(
            Level.WARNING, String.format("Could not load <%s>", functionDefinition.getName()), e);
      }
    }

    return toolSpecifications;
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
