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
import static us.fatehi.utility.Utility.requireNotBlank;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import schemacrawler.tools.ai.model.Document;
import schemacrawler.tools.ai.tools.FunctionReturn;

public class JsonFunctionReturn implements FunctionReturn {

  private final ObjectNode objectNode;

  public JsonFunctionReturn(final Document document) {
    requireNonNull(document, "No schema document provided");
    objectNode = document.toObjectNode();
  }

  public JsonFunctionReturn(final ObjectNode objectNode) {
    this.objectNode = requireNonNull(objectNode, "No object node provided");
  }

  public JsonFunctionReturn(final String listName, final ArrayNode list) {
    requireNotBlank(listName, "No list name provided");
    objectNode = mapper.createObjectNode();
    if (list != null) {
      objectNode.set(listName, list);
    }
  }

  @Override
  public String get() {
    return objectNode.toString();
  }

  public ObjectNode getObjectNode() {
    return objectNode;
  }
}
