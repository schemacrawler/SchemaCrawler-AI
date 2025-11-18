/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.ArrayList;
import java.util.List;

public class JsonSchemaGenerator {

  private static final ObjectMapper mapper = new ObjectMapper();

  public static JsonNode generateSchema(final Class<?> clazz) {

    final SerializationConfig config = mapper.getSerializationConfig();
    final BeanDescription beanDesc =
        config.introspect(TypeFactory.defaultInstance().constructType(clazz));

    final ObjectNode schemaNode = mapper.createObjectNode();

    schemaNode.put("type", "object");
    final ObjectNode propertiesNode = schemaNode.putObject("properties");
    final List<String> required = new ArrayList<>();
    final List<BeanPropertyDefinition> propertyDefinitions = beanDesc.findProperties();
    for (final BeanPropertyDefinition propertyDefinition : propertyDefinitions) {
      System.out.println(propertyDefinition.getGetter().getFullName());

      final String propertyName = propertyDefinition.getName();
      final JavaType javaType = propertyDefinition.getPrimaryType();
      final PropertyMetadata propertyMetadata = propertyDefinition.getMetadata();

      final ObjectNode parameterNode = propertiesNode.putObject(propertyName);
      setType(parameterNode, javaType);

      final String description = propertyMetadata.getDescription();
      if (description != null && !description.strip().isBlank()) {
        parameterNode.put("description", description.replaceAll("\\R", " ").strip());
      }

      setEnumValues(parameterNode, javaType);
      setItems(parameterNode, javaType);

      if (propertyMetadata.isRequired()) {
        required.add(propertyName);
      }
    }

    final ArrayNode requiredArray = schemaNode.putArray("required");
    required.forEach(requiredArray::add);

    schemaNode.put("additionalProperties", false);

    return schemaNode;
  }

  private static void setEnumValues(final ObjectNode node, final JavaType javaType) {
    if (javaType.isEnumType()) {
      final ArrayNode enumValuesNode = node.putArray("enum");
      final Object[] constants = javaType.getRawClass().getEnumConstants();
      for (final Object e : constants) {
        enumValuesNode.add(((Enum<?>) e).name());
      }
    }
  }

  private static void setItems(final ObjectNode node, final JavaType javaType) {
    if (javaType.isArrayType() || javaType.isCollectionLikeType()) {
      final ObjectNode itemsNode = node.putObject("items");
      final JavaType contentType = javaType.getContentType();
      setType(itemsNode, contentType);
      setEnumValues(itemsNode, contentType);
    }
  }

  private static void setType(final ObjectNode node, final JavaType javaType) {
    final Class<?> type = javaType.getRawClass();

    final String typeName;
    if (Number.class.isAssignableFrom(type) || type.isPrimitive() && !type.equals(boolean.class)) {
      typeName = "number";
    } else if (type.equals(String.class)) {
      typeName = "string";
    } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
      typeName = "boolean";
    } else if (javaType.isArrayType() || javaType.isCollectionLikeType()) {
      typeName = "array";
    } else {
      typeName = "string";
    }

    node.put("type", typeName);
  }
}
