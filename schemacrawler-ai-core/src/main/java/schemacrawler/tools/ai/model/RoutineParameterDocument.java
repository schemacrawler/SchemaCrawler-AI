/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.model;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineParameter;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"parameter", "mode", "type", "remarks"})
public final class RoutineParameterDocument implements Serializable {

  private static final long serialVersionUID = 5110252842937512910L;

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

  @JsonProperty("parameter")
  public String getColumnName() {
    return routineParameterName;
  }

  @JsonProperty("type")
  public String getDataType() {
    return dataType;
  }

  @JsonProperty("mode")
  public String getParameterMode() {
    return parameterMode;
  }

  @JsonProperty("remarks")
  public String getRemarks() {
    return remarks;
  }
}
