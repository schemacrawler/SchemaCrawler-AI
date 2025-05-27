/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.aichat.tools;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
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
      parameters = new ObjectMapper().writeValueAsString(parametersObject);
    } catch (final Exception e) {
      parameters =
          new KebabCaseStrategy()
              .translate(getParametersClass().getSimpleName())
              .replace("-function-parameters", "");
    }
    toString = String.format("function %s%n\"%s\"%n%s", getName(), getDescription(), parameters);
  }
}
