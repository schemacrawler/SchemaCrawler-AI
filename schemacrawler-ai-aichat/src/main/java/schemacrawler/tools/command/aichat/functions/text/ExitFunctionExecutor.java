/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.functions.text;

import schemacrawler.tools.ai.tools.AbstractFunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.FunctionReturnType;
import schemacrawler.tools.ai.tools.NoParameters;
import us.fatehi.utility.property.PropertyName;

public final class ExitFunctionExecutor extends AbstractFunctionExecutor<NoParameters> {

  protected ExitFunctionExecutor(final PropertyName functionName) {
    super(functionName, FunctionReturnType.TEXT);
  }

  @Override
  public FunctionReturn call() {
    return () -> "Thank you for using SchemaCrawler with AI chat.";
  }
}
