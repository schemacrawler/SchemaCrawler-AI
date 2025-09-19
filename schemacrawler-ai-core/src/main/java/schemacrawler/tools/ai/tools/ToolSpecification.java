/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools;

import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public record ToolSpecification(String name, String description, JsonNode parameters) {

  public String getParametersAsString() {
    return parameters.toPrettyString();
  }

  public JsonNode getToolSpecification() {
    final ObjectNode toolSpecification = mapper.createObjectNode();
    toolSpecification.put("name", name);
    toolSpecification.put("description", description);
    toolSpecification.set("parameters", parameters);
    return toolSpecification;
  }

  @Override
  public String toString() {
    return getToolSpecification().toPrettyString();
  }
}
