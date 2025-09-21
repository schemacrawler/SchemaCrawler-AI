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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import schemacrawler.tools.ai.model.Document;
import schemacrawler.tools.ai.tools.FunctionReturn;

public class JsonFunctionReturn implements FunctionReturn {

  private final JsonNode jsonNode;

  public JsonFunctionReturn() {
    jsonNode = mapper.missingNode();
  }

  public JsonFunctionReturn(final Document document) {
    requireNonNull(document, "No schema document provided");
    jsonNode = document.toObjectNode();
  }

  public JsonFunctionReturn(final JsonNode objectNode) {
    jsonNode = requireNonNull(objectNode, "No object node provided");
  }

  public JsonFunctionReturn(final String listName, final ArrayNode list) {
    requireNotBlank(listName, "No list name provided");
    final ObjectNode listNode = mapper.createObjectNode();
    if (list != null) {
      listNode.set(listName, list);
    }
    jsonNode = listNode;
  }

  @Override
  public String get() {
    return jsonNode.toString();
  }

  public JsonNode getResult() {
    return jsonNode;
  }
}
