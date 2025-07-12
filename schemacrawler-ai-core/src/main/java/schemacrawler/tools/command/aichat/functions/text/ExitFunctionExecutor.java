/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */


package schemacrawler.tools.command.aichat.functions.text;

import schemacrawler.tools.command.aichat.tools.AbstractFunctionExecutor;
import schemacrawler.tools.command.aichat.tools.FunctionReturn;
import us.fatehi.utility.property.PropertyName;

public final class ExitFunctionExecutor extends AbstractFunctionExecutor<NoParameters> {

  protected ExitFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public FunctionReturn call() {
    return () -> "Thank you for using SchemaCrawler with AI chat.";
  }
}
