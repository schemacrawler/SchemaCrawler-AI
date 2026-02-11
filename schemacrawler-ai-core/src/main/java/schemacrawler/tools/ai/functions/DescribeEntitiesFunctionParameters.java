/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import static schemacrawler.ermodel.model.EntityType.strong_entity;
import static schemacrawler.ermodel.model.EntityType.subtype;
import static schemacrawler.ermodel.model.EntityType.weak_entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import schemacrawler.ermodel.model.EntityType;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.base.ParameterUtility;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DescribeEntitiesFunctionParameters(
    @JsonPropertyDescription(
            """
            Name of entity to describe, from the ER model.
            May be specified as a regular expression, matching the fully qualified
            entity name (including the schema).
            Try not to match all entities, but instead use a regular expression
            to match a subset or match a single entity, since otherwise results may
            be large.
            """)
        @JsonProperty(required = false)
        String entityName,
    @JsonPropertyDescription(
            """
            Indicates the types of entities to return - for example, strong, weak
            or subtype entities. It can also return associations (or bridge or join tables).
            """)
        @JsonProperty(required = false)
        EntityKind entityKind)
    implements FunctionParameters {

  public enum EntityKind {
    ALL(null),
    STRONG_ENTITY(strong_entity),
    WEAK_ENTITY(weak_entity),
    SUBTYPE(subtype),
    ASSOCIATION(null);

    private final EntityType type;

    EntityKind(final EntityType type) {
      this.type = type;
    }

    public EntityType entityType() {
      return type;
    }
  }

  public DescribeEntitiesFunctionParameters() {
    this(null, null);
  }

  public DescribeEntitiesFunctionParameters {
    if (entityName == null || entityName.isBlank()) {
      entityName = "";
    }
    if (entityKind == null) {
      entityKind = EntityKind.ALL;
    }
  }

  @Override
  public String toString() {
    return ParameterUtility.parametersToString(this);
  }
}
