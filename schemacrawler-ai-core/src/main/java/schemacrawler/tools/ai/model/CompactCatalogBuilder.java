/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.model;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.EnumMap;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.ermodel.model.Entity;
import schemacrawler.ermodel.model.EntityType;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import us.fatehi.utility.Builder;

public final class CompactCatalogBuilder implements Builder<CatalogDocument> {

  public static CompactCatalogBuilder builder(final Catalog catalog, final ERModel erModel) {
    return new CompactCatalogBuilder(catalog, erModel);
  }

  private final Catalog catalog;
  private final ERModel erModel;
  private final EnumMap<AdditionalTableDetails, Boolean> additionalTableDetails;
  private final EnumMap<AdditionalRoutineDetails, Boolean> additionalRoutineDetails;

  private CompactCatalogBuilder(final Catalog catalog, final ERModel erModel) {
    this.catalog = requireNonNull(catalog, "No catalog provided");
    this.erModel = requireNonNull(erModel, "No ER model provided");
    additionalTableDetails = new EnumMap<>(AdditionalTableDetails.class);
    additionalRoutineDetails = new EnumMap<>(AdditionalRoutineDetails.class);
  }

  @Override
  public CatalogDocument build() {
    requireNonNull(catalog, "No catalog provided");

    final CatalogDocument catalogDocument =
        new CatalogDocument(catalog.getDatabaseInfo().getDatabaseProductName());
    for (final Table table : catalog.getTables()) {
      final TableDocument tableDocument = buildTableDocument(table);
      catalogDocument.addTable(tableDocument);
    }
    for (final Routine routine : catalog.getRoutines()) {
      final RoutineDocument routineDocument = buildRoutineDocument(routine);
      catalogDocument.addRoutine(routineDocument);
    }
    return catalogDocument;
  }

  public RoutineDocument buildRoutineDocument(final Routine routine) {
    requireNonNull(routine, "No routine provided");
    final RoutineDocument routineDocument = new RoutineDocument(routine, additionalRoutineDetails);
    return routineDocument;
  }

  public TableDocument buildTableDocument(final Table table) {
    requireNonNull(table, "No table provided");
    final EntityType entityType = erModel.lookupEntity(table).map(Entity::getType).orElse(null);
    final TableDocument tableDocument =
        new TableDocument(table, entityType, additionalTableDetails);
    return tableDocument;
  }

  public CatalogDocument createCatalogDocument(final Catalog catalog) {
    requireNonNull(catalog, "No catalog provided");

    final CatalogDocument catalogDocument =
        new CatalogDocument(catalog.getDatabaseInfo().getDatabaseProductName());
    for (final Table table : catalog.getTables()) {
      final TableDocument tableDocument = buildTableDocument(table);
      catalogDocument.addTable(tableDocument);
    }
    for (final Routine routine : catalog.getRoutines()) {
      final RoutineDocument routineDocument = buildRoutineDocument(routine);
      catalogDocument.addRoutine(routineDocument);
    }
    return catalogDocument;
  }

  public CompactCatalogBuilder withAdditionalRoutineDetails(
      final Collection<AdditionalRoutineDetails> withAdditionalRoutineDetails) {
    if (withAdditionalRoutineDetails == null || withAdditionalRoutineDetails.isEmpty()) {
      return this;
    }
    for (final AdditionalRoutineDetails additionalRoutineDetail : withAdditionalRoutineDetails) {
      additionalRoutineDetails.put(additionalRoutineDetail, true);
    }

    return this;
  }

  public CompactCatalogBuilder withAdditionalTableDetails(
      final Collection<AdditionalTableDetails> withAdditionalTableDetails) {
    if (withAdditionalTableDetails == null || withAdditionalTableDetails.isEmpty()) {
      return this;
    }
    for (final AdditionalTableDetails additionalTableDetail : withAdditionalTableDetails) {
      additionalTableDetails.put(additionalTableDetail, true);
    }
    return this;
  }
}
