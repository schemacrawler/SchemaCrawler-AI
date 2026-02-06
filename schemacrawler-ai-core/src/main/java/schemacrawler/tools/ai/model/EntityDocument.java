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
import static us.fatehi.utility.Utility.isBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import schemacrawler.ermodel.model.Entity;
import schemacrawler.ermodel.model.EntityAttribute;
import schemacrawler.ermodel.model.EntitySubtype;
import schemacrawler.ermodel.model.EntityType;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.node.ObjectNode;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"schema", "name", "entity-type", "supertype", "attributes"})
public final class EntityDocument implements Document {

  @Serial private static final long serialVersionUID = -6765691827862270251L;

  private final String schemaName;
  private final String entityName;
  private final EntityType entityType;
  private final String supertype;
  private final Collection<EntityAttributeDocument> entityAttributes;

  EntityDocument(final Entity entity) {
    requireNonNull(entity, "No entity provided");

    final String schema = entity.getTable().getSchema().getFullName();
    if (!isBlank(schema)) {
      schemaName = schema;
    } else {
      schemaName = null;
    }
    entityName = entity.getName();
    entityType = entity.getType();

    if (entity instanceof final EntitySubtype subtype) {
      supertype = subtype.getSupertype().getName();
    } else {
      supertype = null;
    }

    final Collection<EntityAttributeDocument> entityAttributes = new ArrayList<>();
    for (final EntityAttribute entityAttribute : entity.getEntityAttributes()) {
      entityAttributes.add(new EntityAttributeDocument(entityAttribute));
    }
    if (entityAttributes.isEmpty()) {
      this.entityAttributes = null;
    } else {
      this.entityAttributes = entityAttributes;
    }
  }

  @JsonProperty("attributes")
  public Collection<EntityAttributeDocument> getEntityAttributes() {
    return entityAttributes;
  }

  @JsonProperty("entity-type")
  public EntityType getEntityType() {
    return entityType;
  }

  @Override
  public String getName() {
    return entityName;
  }

  @JsonProperty("schema")
  public String getSchemaName() {
    return schemaName;
  }

  @JsonProperty("supertype")
  public String getSupertype() {
    return supertype;
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
