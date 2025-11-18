/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyMetadata;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.BasicClassIntrospector;
import tools.jackson.databind.introspect.BeanPropertyDefinition;
import tools.jackson.databind.introspect.ClassIntrospector;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

public class JsonSchemaGenerator {

  private static final Logger LOGGER =
      Logger.getLogger(JsonSchemaGenerator.class.getCanonicalName());

  private static final ObjectMapper mapper = new ObjectMapper();

  public static JsonNode generateSchema(final Class<?> clazz) {

    final BeanDescription beanDesc = createBeanDescription(clazz);

    final ObjectNode schemaNode = mapper.createObjectNode();

    schemaNode.put("type", "object");
    final ObjectNode propertiesNode = schemaNode.putObject("properties");
    final List<String> required = new ArrayList<>();
    final List<BeanPropertyDefinition> propertyDefinitions = beanDesc.findProperties();
    for (final BeanPropertyDefinition propertyDefinition : propertyDefinitions) {
      LOGGER.log(Level.FINE, propertyDefinition.getGetter().getFullName());

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

  private static BeanDescription createBeanDescription(final Class<?> clazz) {

    final SerializationConfig config = mapper.serializationConfig();
    final ClassIntrospector introspector = new BasicClassIntrospector().forOperation(config);

    final JavaType type = mapper.getTypeFactory().constructType(clazz);
    final AnnotatedClass classDef = introspector.introspectClassAnnotations(type);
    final BeanDescription beanDesc = introspector.introspectForSerialization(type, classDef);

    return beanDesc;
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
