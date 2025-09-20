/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.langchain4j;

import static schemacrawler.tools.ai.tools.ToolUtility.extractParametersSchemaMap;

import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonEnumSchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchemaElement;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import dev.langchain4j.service.tool.ToolExecutor;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionDefinitionRegistry;
import schemacrawler.tools.ai.tools.FunctionReturnType;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class Langchain4JUtility {

  private static final Logger LOGGER =
      Logger.getLogger(Langchain4JUtility.class.getCanonicalName());

  public static Map<String, ToolExecutor> toolExecutors(
      final Catalog catalog, final Connection connection) {

    final Map<String, ToolExecutor> toolSpecificationsMap = new HashMap<>();
    for (final FunctionDefinition<?> functionDefinition :
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry()
            .getFunctionDefinitions(FunctionReturnType.TEXT)) {
      final String functionName = functionDefinition.getName();

      final ToolExecutor executor = new Langchain4JToolExecutor(functionName, catalog, connection);
      toolSpecificationsMap.put(functionName, executor);
    }

    return toolSpecificationsMap;
  }

  public static List<ToolSpecification> tools() {

    final List<ToolSpecification> toolSpecifications = new ArrayList<>();
    for (final FunctionDefinition<?> functionDefinition :
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry()
            .getFunctionDefinitions(FunctionReturnType.TEXT)) {

      try {
        final Class<?> parametersClass = functionDefinition.getParametersClass();
        final Map<String, JsonNode> parametersSchema = extractParametersSchemaMap(parametersClass);
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
      final JsonSchemaElement jsonSchemaElement;
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
