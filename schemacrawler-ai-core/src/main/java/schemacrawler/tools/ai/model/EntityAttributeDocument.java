/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.model;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serial;
import java.util.List;
import schemacrawler.ermodel.model.EntityAttribute;
import schemacrawler.ermodel.model.EntityAttributeType;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.node.ObjectNode;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"name", "type", "optional", "default-value", "enum-values"})
public final class EntityAttributeDocument implements Document {

  @Serial private static final long serialVersionUID = -6765691827862270251L;

  private final String entityAttributeName;
  private final EntityAttributeType type;
  private final boolean isOptional;
  private final String defaultValue;
  private final List<String> enumValues;

  EntityAttributeDocument(final EntityAttribute entityAttribute) {
    requireNonNull(entityAttribute, "No entity provided");

    entityAttributeName = entityAttribute.getName();
    type = entityAttribute.getType();
    isOptional = entityAttribute.isOptional();
    defaultValue = entityAttribute.getDefaultValue();

    final List<String> values = entityAttribute.getEnumValues();
    if (values == null || values.isEmpty()) {
      enumValues = null;
    } else {
      enumValues = List.copyOf(values);
    }
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  @JsonProperty("type")
  public EntityAttributeType getEntityAttributeType() {
    return type;
  }

  public List<String> getEnumValues() {
    return enumValues;
  }

  @Override
  public String getName() {
    return entityAttributeName;
  }

  @JsonProperty("optional")
  public boolean isOptional() {
    return isOptional;
  }

  @Override
  public ObjectNode toObjectNode() {
    return mapper.valueToTree(this);
  }

  @Override
  public String toString() {
    return toObjectNode().toString();
  }
}
