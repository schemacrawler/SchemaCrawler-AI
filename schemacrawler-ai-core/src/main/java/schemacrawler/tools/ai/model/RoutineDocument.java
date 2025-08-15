/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.model;

import static schemacrawler.tools.ai.model.AdditionalRoutineDetails.DEFINIITION;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import static us.fatehi.utility.Utility.trimToEmpty;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineParameter;
import schemacrawler.utility.MetaDataUtility;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"schema", "name", "type", "remarks", "parameters", "definition"})
public final class RoutineDocument implements Serializable {

  private static final long serialVersionUID = 1873929712139211255L;

  public static Map<AdditionalRoutineDetails, Boolean> allRoutineDetails() {
    final Map<AdditionalRoutineDetails, Boolean> details;
    details = new EnumMap<>(AdditionalRoutineDetails.class);

    for (final AdditionalRoutineDetails additionalRoutineDetails :
        AdditionalRoutineDetails.values()) {
      if (!details.containsKey(additionalRoutineDetails)) {
        details.put(additionalRoutineDetails, true);
      }
    }
    return details;
  }

  private final String schemaName;
  private final String name;
  private final String type;
  private final String remarks;
  private final List<RoutineParameterDocument> parameters;
  private final String definition;

  RoutineDocument(
      final Routine routine, final Map<AdditionalRoutineDetails, Boolean> routineDetails) {
    Objects.requireNonNull(routine, "No routine provided");
    final Map<AdditionalRoutineDetails, Boolean> details = defaults(routineDetails);

    final String schemaName = routine.getSchema().getFullName();
    this.schemaName = trimToEmpty(schemaName);

    name = routine.getName();
    type = MetaDataUtility.getSimpleTypeName(routine).name();

    parameters = new ArrayList<>();
    for (final RoutineParameter routineParameter : routine.getParameters()) {
      final RoutineParameterDocument parameterDocument =
          new RoutineParameterDocument(routineParameter);
      parameters.add(parameterDocument);
    }

    if (routine.hasRemarks()) {
      final String remarks = routine.getRemarks();
      this.remarks = trimToEmpty(remarks);
    } else {
      remarks = null;
    }

    if (details.get(DEFINIITION) && routine.hasDefinition()) {
      definition = routine.getDefinition();
    } else {
      definition = null;
    }
  }

  public String getDefinition() {
    return definition;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public List<RoutineParameterDocument> getParameters() {
    return parameters;
  }

  public String getRemarks() {
    return remarks;
  }

  @JsonProperty("schema")
  public String getSchema() {
    return schemaName;
  }

  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public JsonNode toJson() {
    return new ObjectMapper().valueToTree(this);
  }

  @Override
  public String toString() {
    return toJson().toString();
  }

  private Map<AdditionalRoutineDetails, Boolean> defaults(
      final Map<AdditionalRoutineDetails, Boolean> routineDetails) {
    final Map<AdditionalRoutineDetails, Boolean> details;
    if (routineDetails == null) {
      details = new EnumMap<>(AdditionalRoutineDetails.class);
    } else {
      details = routineDetails;
    }

    for (final AdditionalRoutineDetails additionalRoutineDetails :
        AdditionalRoutineDetails.values()) {
      if (!details.containsKey(additionalRoutineDetails)) {
        details.put(additionalRoutineDetails, false);
      }
    }
    return details;
  }
}
