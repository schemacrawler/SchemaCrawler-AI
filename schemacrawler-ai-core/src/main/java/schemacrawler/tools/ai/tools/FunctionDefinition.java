/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */



package schemacrawler.tools.ai.tools;

import schemacrawler.schema.TypedObject;
import us.fatehi.utility.property.PropertyName;

public interface FunctionDefinition<P extends FunctionParameters>
    extends TypedObject<FunctionReturnType> {

  String getDescription();

  default PropertyName getFunctionName() {
    return new PropertyName(getName(), getDescription());
  }

  FunctionReturnType getFunctionReturnType();

  String getName();

  Class<P> getParametersClass();

  @Override
  default FunctionReturnType getType() {
    return getFunctionReturnType();
  }

  FunctionExecutor<P> newExecutor();
}
