/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools.base;

import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static tools.jackson.databind.util.NamingStrategyImpls.KEBAB_CASE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionParameters;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

public abstract class AbstractFunctionDefinition<P extends FunctionParameters>
    implements FunctionDefinition<P> {

  private JsonNode definition;

  @JsonIgnore
  @Override
  public abstract String getDescription();

  @JsonIgnore
  @Override
  public final String getName() {
    return KEBAB_CASE
        .translate(this.getClass().getSimpleName())
        .replace("-function-definition", "");
  }

  @Override
  public String toString() {
    if (definition == null) {
      definition = buildDefinition();
    }
    return definition.toPrettyString();
  }

  private JsonNode buildDefinition() {
    final ObjectNode objectNode = mapper.createObjectNode();

    objectNode.put("name", getName());
    objectNode.put("title", getTitle());
    objectNode.put("description", getDescription());

    return objectNode;
  }
}
