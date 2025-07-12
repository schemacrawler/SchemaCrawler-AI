/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */



package schemacrawler.tools.command.aichat.functions.text;

import com.fasterxml.jackson.annotation.JsonIgnore;
import schemacrawler.tools.ai.tools.AbstractFunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.FunctionReturnType;

public abstract class AbstractTextFunctionDefinition<P extends FunctionParameters>
    extends AbstractFunctionDefinition<P> {

  @JsonIgnore
  @Override
  public final FunctionReturnType getFunctionReturnType() {
    return FunctionReturnType.TEXT;
  }
}
