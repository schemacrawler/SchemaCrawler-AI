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
import java.util.UUID;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.KebabCaseStrategy;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.command.chatgpt.FunctionExecutor;
import schemacrawler.tools.command.chatgpt.FunctionParameters;
import us.fatehi.utility.property.PropertyName;

public abstract class AbstractFunctionExecutor<P extends FunctionParameters>
    implements FunctionExecutor<P> {

  private final PropertyName functionName;
  private final UUID executorInstanceId;
  // Running state
  protected P args;
  protected Catalog catalog;
  protected Connection connection;

  protected AbstractFunctionExecutor(final PropertyName functionName) {
    this.functionName = requireNonNull(functionName, "Function name cannot be null");
    executorInstanceId = UUID.randomUUID();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final AbstractFunctionExecutor<?> other = (AbstractFunctionExecutor<?>) obj;
    return Objects.equals(args, other.args);
  }

  @Override
  public final String getDescription() {
    return functionName.getDescription();
  }

  @Override
  public final UUID getExecutorInstanceId() {
    return executorInstanceId;
  }

  @Override
  public final String getName() {
    return functionName.getName();
  }

  @Override
  public int hashCode() {
    return Objects.hash(args);
  }

  @Override
  public String toString() {
    return String.format(
        "function %s(%s)%n\"%s\"",
        getName(),
        new KebabCaseStrategy().translate(args.getClass().getSimpleName()),
        getDescription());
  }

  @Override
  public void initialize(final P args, final Catalog catalog, final Connection connection) {
    this.args = requireNonNull(args, "No parameters provided");
    if (catalog == null) {
      throw new ExecutionRuntimeException("Catalog is not provided");
    }
    this.catalog = catalog;
    // Connection can be null
    this.connection = connection;
  }
}
