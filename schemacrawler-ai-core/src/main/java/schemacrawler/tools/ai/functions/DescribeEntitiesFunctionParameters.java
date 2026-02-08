/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import schemacrawler.ermodel.model.EntityType;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.base.ParameterUtility;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
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
            or subtype entities. Use "unknown" to return all types of entities.
            """)
        @JsonProperty(required = false)
        EntityType entityType)
    implements FunctionParameters {

  public DescribeEntitiesFunctionParameters() {
    this(null, null);
  }

  public DescribeEntitiesFunctionParameters {
    if (entityName == null || entityName.isBlank()) {
      entityName = "";
    }
    if (entityType == null) {
      entityType = EntityType.unknown;
    }
  }

  @Override
  public String toString() {
    return ParameterUtility.parametersToString(this);
  }
}
