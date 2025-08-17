/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.ai.tools.AbstractSchemaCrawlerFunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.FunctionReturnType;
import schemacrawler.utility.MetaDataUtility;
import us.fatehi.utility.property.PropertyName;

public abstract class AbstractJsonFunctionExecutor<P extends FunctionParameters>
    extends AbstractSchemaCrawlerFunctionExecutor<P> {

  protected AbstractJsonFunctionExecutor(
      final PropertyName functionName, final FunctionReturnType returnType) {
    super(functionName, returnType);
  }

  protected final void refilterCatalog() {
    final SchemaCrawlerOptions options = createSchemaCrawlerOptions();
    MetaDataUtility.reduceCatalog(catalog, options);
  }
}
