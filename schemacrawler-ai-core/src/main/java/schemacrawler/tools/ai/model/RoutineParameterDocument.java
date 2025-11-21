/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.model;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static us.fatehi.utility.Utility.isBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serial;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineParameter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.node.ObjectNode;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"name", "mode", "data-type", "remarks"})
public final class RoutineParameterDocument implements Document {

  @Serial private static final long serialVersionUID = 5110252842937512910L;

  private final String routineParameterName;
  private final String parameterMode;
  private final String dataType;
  private final String remarks;

  public RoutineParameterDocument(final RoutineParameter<? extends Routine> routineParameter) {
    requireNonNull(routineParameter, "No routine parameter provided");

    routineParameterName = routineParameter.getName();
    parameterMode = routineParameter.getParameterMode().name();
    dataType = routineParameter.getColumnDataType().getName();

    final String remarks = routineParameter.getRemarks();
    if (!isBlank(remarks)) {
      this.remarks = remarks;
    } else {
      this.remarks = null;
    }
  }

  public String getDataType() {
    return dataType;
  }

  @Override
  public String getName() {
    return routineParameterName;
  }

  @JsonProperty("mode")
  public String getParameterMode() {
    return parameterMode;
  }

  @JsonProperty("remarks")
  public String getRemarks() {
    return remarks;
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
