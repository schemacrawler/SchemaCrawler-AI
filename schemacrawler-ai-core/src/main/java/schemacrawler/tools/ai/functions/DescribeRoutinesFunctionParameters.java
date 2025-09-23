/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.ArrayList;
import java.util.Collection;
import schemacrawler.tools.ai.model.AdditionalRoutineDetails;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.FunctionReturnType;
import schemacrawler.tools.ai.utility.JsonUtility;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record DescribeRoutinesFunctionParameters(
    @JsonPropertyDescription(
            """
            Name of database routine (stored procedure or function) to describe.
            May be specified as a regular expression matching the fully qualified
            stored procedure or function names (including the schema).
            Use an empty string if all routines are requested.
            If not specified, all routines are returned, but the results
            could be large.
            """)
        @JsonProperty(required = false)
        String routineName,
    @JsonPropertyDescription(
            """
            Indicates what details of the database stored procedure or function
            to return - parameters (including return types), attributes,
            and routine definition.
            Parameters, return types, and remarks or comments are always returned.
            """)
        @JsonProperty(required = false)
        Collection<RoutineDescriptionScope> descriptionScope)
    implements FunctionParameters {

  public enum RoutineDescriptionScope {
    DEFAULT(null),
    REFERENCED_OBJECTS(AdditionalRoutineDetails.REFERENCED_OBJECTS),
    ATTRIBUTES(AdditionalRoutineDetails.ATTRIBUTES),
    DEFINIITION(AdditionalRoutineDetails.DEFINIITION);

    private final AdditionalRoutineDetails additionalRoutineDetails;

    RoutineDescriptionScope(final AdditionalRoutineDetails additionalRoutineDetails) {
      this.additionalRoutineDetails = additionalRoutineDetails;
    }

    public AdditionalRoutineDetails toAdditionalRoutineDetails() {
      return additionalRoutineDetails;
    }
  }

  public DescribeRoutinesFunctionParameters {
    if (routineName == null || routineName.isBlank()) {
      routineName = "";
    }
    if (descriptionScope == null) {
      descriptionScope = new ArrayList<>();
    }
  }

  @Override
  public String toString() {
    return JsonUtility.parametersToString(this);
  }

  @JsonIgnore
  @Override
  public final FunctionReturnType getFunctionReturnType() {
    return FunctionReturnType.JSON;
  }
}
