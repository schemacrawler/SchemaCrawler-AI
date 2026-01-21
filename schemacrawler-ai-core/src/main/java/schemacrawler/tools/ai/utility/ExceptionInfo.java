/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"error-message", "error-type"})
public class ExceptionInfo {

  public final String type;
  public final String message;

  public ExceptionInfo(final Exception ex) {
    if (ex == null) {
      type = "";
      message = "An unknown error occurred";
    } else {
      type = ex.getClass().getName();
      message = ex.getMessage();
    }
  }

  @JsonProperty("error-message")
  public String getMessage() {
    return message;
  }

  @JsonProperty("error-type")
  public String getType() {
    return type;
  }
}
