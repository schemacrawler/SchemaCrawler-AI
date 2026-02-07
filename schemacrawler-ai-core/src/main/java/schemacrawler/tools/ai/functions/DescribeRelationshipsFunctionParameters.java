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
import schemacrawler.ermodel.model.RelationshipCardinality;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.base.ParameterUtility;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record DescribeRelationshipsFunctionParameters(
    @JsonPropertyDescription(
            """
            Name of relationships to describe, from the ER model.
            May be specified as a regular expression, matching the fully qualified
            relationship name (including the schema).
            Try not to match all relationships, but instead use a regular expression
            to match a subset or match a single relationships, since otherwise results may
            be large.
            """)
        @JsonProperty(required = false)
        String relationshipName,
    @JsonPropertyDescription(
            """
            Indicates the types of relationships to return - for example, 1..1, 1..M, M..N
            and optional relationships.
            Use "unknown" to return all types of relationships.
            """)
        @JsonProperty(required = false)
        RelationshipCardinality cardinality)
    implements FunctionParameters {

  public DescribeRelationshipsFunctionParameters {
    if (relationshipName == null || relationshipName.isBlank()) {
      relationshipName = "";
    }
    if (cardinality == null) {
      cardinality = RelationshipCardinality.unknown;
    }
  }

  @Override
  public String toString() {
    return ParameterUtility.parametersToString(this);
  }
}
