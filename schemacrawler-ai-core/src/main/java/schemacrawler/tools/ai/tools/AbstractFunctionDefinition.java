/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools;

import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.KebabCaseStrategy;

public abstract class AbstractFunctionDefinition<P extends FunctionParameters>
    implements FunctionDefinition<P> {

  private String toString;

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
    buildToString();
    return toString;
  }

  private void buildToString() {
    if (toString != null) {
      return;
    }

    String parameters;
    try {
      final FunctionParameters parametersObject =
          getParametersClass().getDeclaredConstructor().newInstance();
      parameters = mapper.writeValueAsString(parametersObject);
    } catch (final Exception e) {
      parameters =
          new KebabCaseStrategy()
              .translate(getParametersClass().getSimpleName())
              .replace("-function-parameters", "");
    }
    toString = String.format("function %s%n\"%s\"%n%s", getName(), getDescription(), parameters);
  }
}
