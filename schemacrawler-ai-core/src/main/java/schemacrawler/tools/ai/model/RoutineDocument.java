/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.model;

import static schemacrawler.tools.ai.model.AdditionalRoutineDetails.DEFINIITION;
import static schemacrawler.tools.ai.model.AdditionalRoutineDetails.REFERENCED_OBJECTS;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static schemacrawler.utility.MetaDataUtility.getTypeName;
import static us.fatehi.utility.Utility.trimToEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineParameter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.node.ObjectNode;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
  "schema",
  "name",
  "type",
  "parameters",
  "referenced-objects",
  "remarks",
  "definition"
})
public final class RoutineDocument implements Document {

  @Serial private static final long serialVersionUID = 1873929712139211255L;

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
  private final String routineName;
  private final String type;
  private final List<RoutineParameterDocument> parameters;
  private final Collection<DatabaseObjectDocument> referencedObjects;
  private final String remarks;
  private final String definition;

  RoutineDocument(
      final Routine routine, final Map<AdditionalRoutineDetails, Boolean> routineDetails) {
    Objects.requireNonNull(routine, "No routine provided");
    final Map<AdditionalRoutineDetails, Boolean> details = defaults(routineDetails);

    final String schemaName = routine.getSchema().getFullName();
    this.schemaName = trimToEmpty(schemaName);

    routineName = routine.getName();
    type = getTypeName(routine);

    parameters = new ArrayList<>();
    for (final RoutineParameter<? extends Routine> routineParameter : routine.getParameters()) {
      final RoutineParameterDocument parameterDocument =
          new RoutineParameterDocument(routineParameter);
      parameters.add(parameterDocument);
    }

    if (details.get(REFERENCED_OBJECTS)) {
      final Collection<? extends DatabaseObject> references = routine.getReferencedObjects();
      Collections.sort(new ArrayList<>(references));
      referencedObjects = new ArrayList<>();
      for (final DatabaseObject referencedObject : references) {
        referencedObjects.add(new DatabaseObjectDocument(referencedObject));
      }
    } else {
      referencedObjects = null;
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

  @Override
  public String getName() {
    return routineName;
  }

  public List<RoutineParameterDocument> getParameters() {
    return parameters;
  }

  public Collection<DatabaseObjectDocument> getReferencedObjects() {
    return referencedObjects;
  }

  public String getRemarks() {
    return remarks;
  }

  @JsonProperty("schema")
  public String getSchemaName() {
    return schemaName;
  }

  public String getType() {
    return type;
  }

  @Override
  public ObjectNode toObjectNode() {
    return mapper.valueToTree(this);
  }

  @Override
  public String toString() {
    return toObjectNode().toString();
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
