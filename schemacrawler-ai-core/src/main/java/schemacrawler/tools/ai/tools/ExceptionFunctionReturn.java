/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import schemacrawler.tools.ai.utility.ExceptionInfo;
import tools.jackson.databind.node.ObjectNode;

public record ExceptionFunctionReturn(Exception exception) implements FunctionReturn {

  public ExceptionFunctionReturn {
    exception = requireNonNull(exception, "No exception provided");
  }

  @Override
  public String get() {
    final ObjectNode objectNode = mapper.createObjectNode();
    objectNode.putPOJO("exception", new ExceptionInfo(exception));
    return objectNode.toString();
  }
}
