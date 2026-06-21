/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.model;

import static us.fatehi.utility.Utility.trimToEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import schemacrawler.ermodel.model.Entity;
import schemacrawler.ermodel.model.EntityAttribute;
import schemacrawler.ermodel.model.EntitySubtype;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"full_name", "name", "remarks", "entity_type", "supertype", "attributes"})
public final class EntityDocument extends DatabaseObjectDocument {

  @Serial private static final long serialVersionUID = -6765691827862270251L;

  private final String supertype;
  private final Collection<EntityAttributeDocument> entityAttributes;
  private final String remarks;

  EntityDocument(final Entity entity) {
    super(entity);

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

    if (entity.hasRemarks()) {
      final String remarks = entity.getRemarks();
      this.remarks = trimToEmpty(remarks);
    } else {
      remarks = null;
    }
  }

  @JsonProperty("attributes")
  public Collection<EntityAttributeDocument> getEntityAttributes() {
    return entityAttributes;
  }

  public String getRemarks() {
    return remarks;
  }

  @JsonProperty("supertype")
  public String getSupertype() {
    return supertype;
  }

  @Override
  @JsonProperty("entity_type")
  public String getType() {
    return super.getType();
  }
}
