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

package schemacrawler.tools.command.chatgpt.functions;

import java.sql.Connection;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.KebabCaseStrategy;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.chatgpt.FunctionDefinition;
import schemacrawler.tools.command.chatgpt.FunctionParameters;

public abstract class AbstractFunctionDefinition<P extends FunctionParameters>
    implements FunctionDefinition<P> {

  private final Class<P> parameters;
  protected Catalog catalog;
  protected Connection connection;

  protected AbstractFunctionDefinition(final Class<P> parameters) {
    this.parameters = requireNonNull(parameters, "Function parameters not provided");
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final AbstractFunctionDefinition<?> other = (AbstractFunctionDefinition<?>) obj;
    return Objects.equals(parameters, other.parameters);
  }

  @Override
  public Catalog getCatalog() {
    return catalog;
  }

  public Connection getConnection() {
    return connection;
  }

  @JsonIgnore
  @Override
  public abstract String getDescription();

  @JsonIgnore
  @Override
  public final String getName() {
    return new KebabCaseStrategy().translate(this.getClass().getSimpleName());
  }

  @Override
  public Class<P> getParametersClass() {
    return parameters;
  }

  @Override
  public int hashCode() {
    return Objects.hash(parameters);
  }

  @Override
  public void setCatalog(final Catalog catalog) {
    this.catalog = catalog;
  }

  @Override
  public void setConnection(final Connection connection) {
    this.connection = connection;
  }

  @Override
  public String toString() {
    return String.format("function %s(%s)%n\"%s\"", getName(),
        new KebabCaseStrategy().translate(parameters.getSimpleName()), getDescription());
  }
}
