/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
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

  String getTitle();

  FunctionExecutor<P> newExecutor();
}
