/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import schemacrawler.tools.ai.tools.base.AbstractFunctionExecutor;
import schemacrawler.utility.MetaDataUtility;
import us.fatehi.utility.property.PropertyName;

public abstract class AbstractJsonFunctionExecutor<P extends FunctionParameters>
    extends AbstractFunctionExecutor<P> {

  protected AbstractJsonFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public abstract JsonFunctionReturn call() throws Exception;

  protected final void refilterCatalog() {
    final SchemaCrawlerOptions options = createSchemaCrawlerOptions();
    MetaDataUtility.reduceCatalog(catalog, options);
  }
}
