/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */


package schemacrawler.tools.command.aichat.functions.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import schemacrawler.tools.command.aichat.tools.AbstractFunctionDefinition;
import schemacrawler.tools.command.aichat.tools.FunctionParameters;
import schemacrawler.tools.command.aichat.tools.FunctionReturnType;

public abstract class AbstractJsonFunctionDefinition<P extends FunctionParameters>
    extends AbstractFunctionDefinition<P> {

  @JsonIgnore
  @Override
  public final FunctionReturnType getFunctionReturnType() {
    return FunctionReturnType.JSON;
  }
}
