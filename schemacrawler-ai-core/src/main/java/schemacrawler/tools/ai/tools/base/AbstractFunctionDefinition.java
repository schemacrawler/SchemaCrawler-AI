/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools.base;

import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.KebabCaseStrategy;
import com.fasterxml.jackson.databind.node.ObjectNode;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionParameters;

public abstract class AbstractFunctionDefinition<P extends FunctionParameters>
    implements FunctionDefinition<P> {

  private JsonNode definition;

  @JsonIgnore
  @Override
  public abstract String getDescription();

  @JsonIgnore
  @Override
  public final String getName() {
    return new KebabCaseStrategy()
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
    objectNode.put("description", getDescription());

    return objectNode;
  }
}
