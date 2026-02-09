/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.functions;

import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import java.util.ArrayList;
import java.util.Collection;
import schemacrawler.ermodel.model.EntityType;
import schemacrawler.ermodel.model.RelationshipCardinality;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.ai.functions.DescribeEntitiesFunctionParameters.EntityKind;
import schemacrawler.tools.ai.model.CompactERModelBuilder;
import schemacrawler.tools.ai.model.Document;
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

    final EntityKind entityKind = commandOptions.entityKind();
    final InclusionRule inclusionRule = makeInclusionRule(commandOptions.entityName());
    final Collection<? extends Document> documents = new ArrayList<>();
    switch (entityKind) {
      case ALL:
        documents.addAll(
            CompactERModelBuilder.builder(erModel)
                .withEntityTypes(null) // All entities
                .withEntityInclusionRule(inclusionRule)
                .buildEntityDocuments());
      // IMPORTANT: Deliberate fall-through
      case ASSOCIATION:
        documents.addAll(
            CompactERModelBuilder.builder(erModel)
                .withRelationshipCardinalities(RelationshipCardinality.many_many)
                .withRelationshipInclusionRule(inclusionRule)
                .buildRelationshipDocuments());
        break;
      default:
        final EntityType entityType = entityKind.entityType();
        documents.addAll(
            CompactERModelBuilder.builder(erModel)
                .withEntityTypes(entityType)
                .withEntityInclusionRule(inclusionRule)
                .buildEntityDocuments());
    }

    final ArrayNode entitiesArray = createDocumentsArray(documents);
    return new JsonFunctionReturn(entitiesArray);
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
  }

  private ArrayNode createDocumentsArray(final Collection<? extends Document> documents) {
    final ArrayNode list = mapper.createArrayNode();
    if (documents == null || documents.isEmpty()) {
      return list;
    }

    for (final Document entityDocument : documents) {
      if (entityDocument == null) {
        continue;
      }
      list.add(mapper.valueToTree(entityDocument));
    }

    return list;
  }
}
