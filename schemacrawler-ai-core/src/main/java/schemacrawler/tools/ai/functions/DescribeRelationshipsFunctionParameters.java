/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import static schemacrawler.ermodel.model.RelationshipCardinality.many_many;
import static schemacrawler.ermodel.model.RelationshipCardinality.one_many;
import static schemacrawler.ermodel.model.RelationshipCardinality.one_one;
import static schemacrawler.ermodel.model.RelationshipCardinality.zero_many;
import static schemacrawler.ermodel.model.RelationshipCardinality.zero_one;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import schemacrawler.ermodel.model.RelationshipCardinality;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.base.ParameterUtility;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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
            """)
        @JsonProperty(required = false)
        Cardinality cardinality)
    implements FunctionParameters {

  public enum Cardinality {
    ALL(null),
    ZERO_ONE(zero_one),
    ZERO_MANY(zero_many),
    ONE_ONE(one_one),
    ONE_MANY(one_many),
    MANY_MANY(many_many);

    private final RelationshipCardinality cardinality;

    Cardinality(final RelationshipCardinality cardinality) {
      this.cardinality = cardinality;
    }

    public RelationshipCardinality cardinality() {
      return cardinality;
    }
  }

  public DescribeRelationshipsFunctionParameters() {
    this(null, null);
  }

  public DescribeRelationshipsFunctionParameters {
    if (relationshipName == null || relationshipName.isBlank()) {
      relationshipName = "";
    }
    if (cardinality == null) {
      cardinality = Cardinality.ALL;
    }
  }

  @Override
  public String toString() {
    return ParameterUtility.parametersToString(this);
  }
}
