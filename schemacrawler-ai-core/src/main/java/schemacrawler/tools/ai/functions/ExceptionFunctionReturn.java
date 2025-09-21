/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.utility.ExceptionInfo;

public class ExceptionFunctionReturn implements FunctionReturn {

  private final Exception exception;

  public ExceptionFunctionReturn(final Exception exception) {
    this.exception = requireNonNull(exception, "No exception provided");
  }

  public Exception getException() {
    return exception;
  }

  @Override
  public String get() {
    final ObjectNode objectNode = mapper.createObjectNode();
    objectNode.putPOJO("exception", new ExceptionInfo(exception));
    return objectNode.toString();
  }
}
