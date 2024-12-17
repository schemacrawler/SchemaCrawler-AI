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

package schemacrawler.tools.command.aichat.functions;

import java.util.UUID;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.KebabCaseStrategy;
import static java.util.Objects.requireNonNull;
import schemacrawler.tools.command.aichat.FunctionExecutor;
import schemacrawler.tools.command.aichat.FunctionParameters;
import schemacrawler.tools.command.aichat.FunctionReturn;
import schemacrawler.tools.executable.BaseCommand;
import us.fatehi.utility.property.PropertyName;

public abstract class AbstractFunctionExecutor<P extends FunctionParameters>
    extends BaseCommand<P, FunctionReturn> implements FunctionExecutor<P> {

  private final PropertyName functionName;
  private final UUID executorInstanceId;

  protected AbstractFunctionExecutor(final PropertyName functionName) {
    super(requireNonNull(functionName, "Function name not provided"));
    this.functionName = functionName;
    executorInstanceId = UUID.randomUUID();
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
  public void initialize() {
    // No-op
  }

  @Override
  public final String toString() {
    return String.format(
        "function %s(%s)%n\"%s\"",
        command.getName(),
        new KebabCaseStrategy().translate(commandOptions.getClass().getSimpleName()),
        getDescription());
  }
}
