/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"error-message", "error-type", "stack-trace"})
public class ExceptionInfo {

  public static String getStackTraceHead(final Throwable ex, final int maxLines) {
    final StringBuilder sb = new StringBuilder();
    sb.append(ex.toString()).append("\n");

    final StackTraceElement[] elements = ex.getStackTrace();
    for (int i = 0; i < Math.min(maxLines, elements.length); i++) {
      sb.append("\tat ").append(elements[i].toString()).append("\n");
    }

    return sb.toString();
  }

  public final String type;
  public final String message;
  public final String stackTrace;

  public ExceptionInfo(final Exception ex) {
    if (ex == null) {
      type = "";
      message = "An unknown error occurred";
      stackTrace = "";
      return;
    }
    type = ex.getClass().getName();
    message = ex.getMessage();
    stackTrace = getStackTraceHead(ex, 8);
  }

  @JsonProperty("error-message")
  public String getMessage() {
    return message;
  }

  @JsonProperty("stack-trace")
  public String getStackTrace() {
    return stackTrace;
  }

  @JsonProperty("error-type")
  public String getType() {
    return type;
  }
}
