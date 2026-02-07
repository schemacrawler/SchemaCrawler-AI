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
import schemacrawler.ermodel.model.RelationshipCardinality;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.ai.model.CompactERModelBuilder;
import schemacrawler.tools.ai.model.RelationshipDocument;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import schemacrawler.tools.ai.tools.base.AbstractJsonFunctionExecutor;
import tools.jackson.databind.node.ArrayNode;
import us.fatehi.utility.property.PropertyName;

public final class DescribeRelationshipsFunctionExecutor
    extends AbstractJsonFunctionExecutor<DescribeRelationshipsFunctionParameters> {

  protected DescribeRelationshipsFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public JsonFunctionReturn call() throws Exception {
    refilterCatalog();

    final RelationshipCardinality cardinality = commandOptions.cardinality();
    final InclusionRule inclusionRule = makeInclusionRule(commandOptions.relationshipName());
    final Collection<RelationshipDocument> entities =
        CompactERModelBuilder.builder(erModel)
            .withRelationshipCardinalities(cardinality)
            .withRelationshipInclusionRule(inclusionRule)
            .buildRelationshipDocuments();
    final ArrayNode relationshipsArray = createRelationshipsArray(entities);
    return new JsonFunctionReturn(relationshipsArray);
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
  }

  private ArrayNode createRelationshipsArray(
      final Collection<RelationshipDocument> relationshipDocuments) {
    final ArrayNode list = mapper.createArrayNode();
    if (relationshipDocuments == null || relationshipDocuments.isEmpty()) {
      return list;
    }

    for (final RelationshipDocument relationshipDocument : relationshipDocuments) {
      if (relationshipDocument == null) {
        continue;
      }
      list.add(mapper.valueToTree(relationshipDocument));
    }

    return list;
  }
}
