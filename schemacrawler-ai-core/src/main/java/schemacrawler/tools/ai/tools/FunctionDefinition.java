/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.tools;

import us.fatehi.utility.property.PropertyName;

public interface FunctionDefinition<P extends FunctionParameters> {

  String getDescription();

  default PropertyName getFunctionName() {
    return new PropertyName(getName(), getDescription());
  }

  String getName();

  Class<P> getParametersClass();

  FunctionExecutor<P> newExecutor();
}
