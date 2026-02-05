/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.ermodel.model.Entity;
import schemacrawler.ermodel.model.EntityType;
import schemacrawler.ermodel.model.Relationship;
import schemacrawler.ermodel.model.RelationshipCardinality;
import us.fatehi.utility.Builder;

public final class CompactERModelBuilder implements Builder<Collection<EntityDocument>> {

  public static CompactERModelBuilder builder(final ERModel erModel) {
    return new CompactERModelBuilder(erModel);
  }

  private final ERModel erModel;
  private EntityType entityType;
  private RelationshipCardinality cardinality;

  private CompactERModelBuilder(final ERModel erModel) {
    this.erModel = requireNonNull(erModel, "No erModel provided");
    entityType = EntityType.unknown;
    cardinality = RelationshipCardinality.unknown;
  }

  @Override
  public Collection<EntityDocument> build() {
    return buildEntityDocuments();
  }

  public EntityDocument buildEntityDocument(final Entity entity) {
    requireNonNull(entity, "No entity provided");
    final EntityDocument entityDocument = new EntityDocument(entity);
    return entityDocument;
  }

  public Collection<EntityDocument> buildEntityDocuments() {
    requireNonNull(erModel, "No ER model provided");

    final Collection<EntityDocument> entityDocuments = new ArrayList<>();
    final Collection<Entity> entities;
    if (entityType == EntityType.unknown) {
      entities = erModel.getEntities();
    } else {
      entities = erModel.getEntitiesByType(entityType);
    }

    for (final Entity entity : entities) {
      entityDocuments.add(buildEntityDocument(entity));
    }

    return List.copyOf(entityDocuments);
  }

  public RelationshipDocument buildRelationshipDocument(final Relationship relationship) {
    requireNonNull(relationship, "No relationship provided");
    final RelationshipDocument relationshipDocument = new RelationshipDocument(relationship);
    return relationshipDocument;
  }

  public Collection<RelationshipDocument> buildRelationshipDocuments() {
    requireNonNull(erModel, "No ER model provided");

    final Collection<RelationshipDocument> relationshipDocuments = new ArrayList<>();
    final Collection<Relationship> relationships;
    if (cardinality == RelationshipCardinality.unknown) {
      relationships = erModel.getRelationships();
    } else {
      relationships = erModel.getRelationshipsByType(cardinality);
    }

    for (final Relationship relationship : relationships) {
      relationshipDocuments.add(buildRelationshipDocument(relationship));
    }

    return List.copyOf(relationshipDocuments);
  }

  public CompactERModelBuilder withEntityTypes(final EntityType entityType) {
    if (entityType == null) {
      this.entityType = EntityType.unknown;
    } else {
      this.entityType = entityType;
    }
    return this;
  }

  public CompactERModelBuilder withRelationshipCardinality(
      final RelationshipCardinality cardinality) {
    if (cardinality == null) {
      this.cardinality = RelationshipCardinality.unknown;
    } else {
      this.cardinality = cardinality;
    }
    return this;
  }
}
