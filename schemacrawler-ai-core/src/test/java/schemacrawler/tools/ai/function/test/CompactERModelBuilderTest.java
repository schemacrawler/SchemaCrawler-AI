/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.function.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Collection;
import org.junit.jupiter.api.Test;
import schemacrawler.ermodel.model.Entity;
import schemacrawler.ermodel.model.EntityType;
import schemacrawler.ermodel.model.Relationship;
import schemacrawler.ermodel.model.RelationshipCardinality;
import schemacrawler.tools.ai.model.CompactERModelBuilder;
import schemacrawler.tools.ai.model.EntityDocument;
import schemacrawler.tools.ai.model.RelationshipDocument;

public class CompactERModelBuilderTest extends AbstractFunctionTest {

  @Test
  public void build() {
    final Collection<EntityDocument> entityDocuments =
        CompactERModelBuilder.builder(erModel).build();
    assertThat(entityDocuments, is(notNullValue()));
    assertThat(entityDocuments, is(not(empty())));
  }

  @Test
  public void buildEntityDocument() {
    final Entity entity = erModel.getEntities().iterator().next();
    final EntityDocument entityDocument =
        CompactERModelBuilder.builder(erModel).buildEntityDocument(entity);
    assertThat(entityDocument, is(notNullValue()));
    assertThat(entityDocument.getName(), is(equalTo(entity.getName())));
  }

  @Test
  public void buildEntityDocuments() {
    final Collection<EntityDocument> entityDocuments =
        CompactERModelBuilder.builder(erModel).buildEntityDocuments();
    assertThat(entityDocuments, is(notNullValue()));
    assertThat(entityDocuments, is(not(empty())));
    assertThat(entityDocuments.size(), is(equalTo(erModel.getEntities().size())));
  }

  @Test
  public void buildRelationshipDocument() {
    final Relationship relationship = erModel.getRelationships().iterator().next();
    final RelationshipDocument relationshipDocument =
        CompactERModelBuilder.builder(erModel).buildRelationshipDocument(relationship);
    assertThat(relationshipDocument, is(notNullValue()));
    assertThat(relationshipDocument.getName(), is(equalTo(relationship.getName())));
  }

  @Test
  public void buildRelationshipDocuments() {
    final Collection<RelationshipDocument> relationshipDocuments =
        CompactERModelBuilder.builder(erModel).buildRelationshipDocuments();
    assertThat(relationshipDocuments, is(notNullValue()));
    assertThat(relationshipDocuments, is(not(empty())));
    assertThat(relationshipDocuments.size(), is(equalTo(erModel.getRelationships().size())));
  }

  @Test
  public void withEntityTypes() {
    final CompactERModelBuilder builder = CompactERModelBuilder.builder(erModel);

    // Get an entity type from the model
    final EntityType type = erModel.getEntities().iterator().next().getType();

    // Filter by type
    builder.withEntityTypes(type);
    final Collection<EntityDocument> filteredEntities = builder.buildEntityDocuments();
    for (final EntityDocument doc : filteredEntities) {
      assertThat(doc.getEntityType(), is(type));
    }

    // Reset filter
    builder.withEntityTypes(null);
    final Collection<EntityDocument> allEntities = builder.buildEntityDocuments();
    assertThat(allEntities.size(), is(equalTo(erModel.getEntities().size())));
  }

  @Test
  public void withRelationshipCardinality() {
    final CompactERModelBuilder builder = CompactERModelBuilder.builder(erModel);

    // Get a cardinality from the model
    final RelationshipCardinality cardinality =
        erModel.getRelationships().iterator().next().getType();

    // Filter by cardinality
    builder.withRelationshipCardinality(cardinality);
    final Collection<RelationshipDocument> filteredRelationships =
        builder.buildRelationshipDocuments();
    for (final RelationshipDocument doc : filteredRelationships) {
      assertThat(doc.getCardinality(), is(cardinality));
    }

    // Reset filter
    builder.withRelationshipCardinality(null);
    final Collection<RelationshipDocument> allRelationships = builder.buildRelationshipDocuments();
    assertThat(allRelationships.size(), is(equalTo(erModel.getRelationships().size())));
  }
}
