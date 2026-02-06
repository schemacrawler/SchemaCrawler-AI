/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import java.util.Collection;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.ai.model.CompactERModelBuilder;
import schemacrawler.tools.ai.model.EntityDocument;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import schemacrawler.tools.ai.tools.base.AbstractJsonFunctionExecutor;
import tools.jackson.databind.node.ArrayNode;
import us.fatehi.utility.property.PropertyName;

public final class DescribeEntitiesFunctionExecutor
    extends AbstractJsonFunctionExecutor<DescribeEntitiesFunctionParameters> {

  protected DescribeEntitiesFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public JsonFunctionReturn call() throws Exception {
    refilterCatalog();

    final Collection<EntityDocument> entities =
        CompactERModelBuilder.builder(erModel).withEntityTypes(commandOptions.entityType()).build();
    final ArrayNode entitiesArray = createEntitiesArray(entities);
    return new JsonFunctionReturn(entitiesArray);
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
  }

  private ArrayNode createEntitiesArray(final Collection<EntityDocument> entityDocuments) {
    final ArrayNode list = mapper.createArrayNode();
    if (entityDocuments == null || entityDocuments.isEmpty()) {
      return list;
    }

    for (final EntityDocument entityDocument : entityDocuments) {
      if (entityDocument == null) {
        continue;
      }
      list.add(mapper.valueToTree(entityDocument));
    }

    return list;
  }
}
