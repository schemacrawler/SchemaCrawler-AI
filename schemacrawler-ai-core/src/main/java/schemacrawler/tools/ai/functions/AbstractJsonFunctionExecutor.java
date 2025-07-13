/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.ai.tools.AbstractSchemaCrawlerFunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.utility.MetaDataUtility;
import us.fatehi.utility.property.PropertyName;

public abstract class AbstractJsonFunctionExecutor<P extends FunctionParameters>
    extends AbstractSchemaCrawlerFunctionExecutor<P> {

  protected AbstractJsonFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  protected final void refilterCatalog() {
    final SchemaCrawlerOptions options = createSchemaCrawlerOptions();
    MetaDataUtility.reduceCatalog(catalog, options);
  }

  protected final ObjectNode wrapList(final ArrayNode list) {
    final ObjectNode objectNode = mapper.createObjectNode();
    objectNode.put("db", catalog.getDatabaseInfo().getDatabaseProductName());
    if (list != null) {
      objectNode.set("list", list);
    }
    return objectNode;
  }
}
