/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.KebabCaseStrategy;
import java.util.UUID;
import schemacrawler.tools.executable.BaseCommand;
import us.fatehi.utility.property.PropertyName;

public abstract class AbstractFunctionExecutor<P extends FunctionParameters>
    extends BaseCommand<P, FunctionReturn> implements FunctionExecutor<P> {

  private final PropertyName functionName;
  private final UUID executorInstanceId;
  private final FunctionReturnType returnType;

  protected AbstractFunctionExecutor(
      final PropertyName functionName, final FunctionReturnType returnType) {
    super(requireNonNull(functionName, "Function name not provided"));
    this.functionName = functionName;
    executorInstanceId = UUID.randomUUID();
    this.returnType = requireNonNull(returnType, "No return type specified");
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
  public FunctionReturnType getFunctionReturnType() {
    return returnType;
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
