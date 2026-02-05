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
import static org.hamcrest.Matchers.nullValue;

import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.tools.ai.model.AdditionalRoutineDetails;
import schemacrawler.tools.ai.model.AdditionalTableDetails;
import schemacrawler.tools.ai.model.CatalogDocument;
import schemacrawler.tools.ai.model.ColumnDocument;
import schemacrawler.tools.ai.model.CompactCatalogBuilder;
import schemacrawler.tools.ai.model.ForeignKeyDocument;
import schemacrawler.tools.ai.model.IndexDocument;
import schemacrawler.tools.ai.model.RoutineDocument;
import schemacrawler.tools.ai.model.TableDocument;
import schemacrawler.tools.ai.model.TriggerDocument;

public class CompactCatalogBuilderTest extends AbstractFunctionTest {

  @Test
  public void build() {
    final CatalogDocument catalogDocument = CompactCatalogBuilder.builder(catalog, erModel).build();
    assertThat(catalogDocument, is(notNullValue()));
    assertThat(catalogDocument.getDatabaseProductName(), is(notNullValue()));
    assertThat(catalogDocument.getTables(), is(not(empty())));
    assertThat(catalogDocument.getRoutines(), is(not(empty())));
  }

  @Test
  public void buildColumnDocument() {
    final Table table = catalog.getTables().iterator().next();
    final Column column = table.getColumns().iterator().next();
    final ColumnDocument columnDocument =
        CompactCatalogBuilder.builder(catalog, erModel).buildColumnDocument(column);
    assertThat(columnDocument, is(notNullValue()));
    assertThat(columnDocument.getName(), is(equalTo(column.getName())));
  }

  @Test
  public void buildForeignKeyDocument() {
    ForeignKey foreignKey = null;
    for (final Table table : catalog.getTables()) {
      final Collection<ForeignKey> foreignKeys = table.getForeignKeys();
      if (!foreignKeys.isEmpty()) {
        foreignKey = foreignKeys.iterator().next();
        break;
      }
    }
    assertThat("No foreign key found in test database", foreignKey, is(notNullValue()));

    final ForeignKeyDocument foreignKeyDocument =
        CompactCatalogBuilder.builder(catalog, erModel).buildForeignKeyDocument(foreignKey);
    assertThat(foreignKeyDocument, is(notNullValue()));
    assertThat(foreignKeyDocument.getName(), is(equalTo(foreignKey.getName())));
    assertThat(foreignKeyDocument.getCardinality(), is(notNullValue()));
  }

  @Test
  public void buildIndexDocument() {
    Index index = null;
    for (final Table table : catalog.getTables()) {
      final Collection<Index> indexes = table.getIndexes();
      if (!indexes.isEmpty()) {
        index = indexes.iterator().next();
        break;
      }
    }
    assertThat("No index found in test database", index, is(notNullValue()));

    final IndexDocument indexDocument =
        CompactCatalogBuilder.builder(catalog, erModel).buildIndexDocument(index);
    assertThat(indexDocument, is(notNullValue()));
    assertThat(indexDocument.getName(), is(equalTo(index.getName())));
  }

  @Test
  public void buildRoutineDocument() {
    final Routine routine = catalog.getRoutines().iterator().next();
    final RoutineDocument routineDocument =
        CompactCatalogBuilder.builder(catalog, erModel).buildRoutineDocument(routine);
    assertThat(routineDocument, is(notNullValue()));
    assertThat(routineDocument.getName(), is(equalTo(routine.getName())));
  }

  @Test
  public void buildTableDocument() {
    final Table table = catalog.getTables().iterator().next();
    final TableDocument tableDocument =
        CompactCatalogBuilder.builder(catalog, erModel).buildTableDocument(table);
    assertThat(tableDocument, is(notNullValue()));
    assertThat(tableDocument.getName(), is(equalTo(table.getName())));
  }

  @Test
  public void buildTriggerDocument() {
    Trigger trigger = null;
    for (final Table table : catalog.getTables()) {
      final Collection<Trigger> triggers = table.getTriggers();
      if (!triggers.isEmpty()) {
        trigger = triggers.iterator().next();
        break;
      }
    }
    assertThat("No trigger found in test database", trigger, is(notNullValue()));

    final TriggerDocument triggerDocument =
        CompactCatalogBuilder.builder(catalog, erModel).buildTriggerDocument(trigger);
    assertThat(triggerDocument, is(notNullValue()));
    assertThat(triggerDocument.getName(), is(equalTo(trigger.getName())));
  }

  @Test
  public void withAdditionalRoutineDetails() {
    final Routine routine = catalog.getRoutines().iterator().next();
    final CompactCatalogBuilder builder = CompactCatalogBuilder.builder(catalog, erModel);

    // Without details
    RoutineDocument routineDocument = builder.buildRoutineDocument(routine);
    assertThat(routineDocument.getDefinition(), is(nullValue()));

    // With details
    builder.withAdditionalRoutineDetails(List.of(AdditionalRoutineDetails.DEFINIITION));
    routineDocument = builder.buildRoutineDocument(routine);
    assertThat(routineDocument.getDefinition(), is(notNullValue()));
  }

  @Test
  public void withAdditionalTableDetails() {
    final Table table = catalog.getTables().iterator().next();
    final CompactCatalogBuilder builder = CompactCatalogBuilder.builder(catalog, erModel);

    // Without details
    TableDocument tableDocument = builder.buildTableDocument(table);
    assertThat(tableDocument.getPrimaryKey(), is(nullValue()));

    // With details
    builder.withAdditionalTableDetails(List.of(AdditionalTableDetails.PRIMARY_KEY));
    tableDocument = builder.buildTableDocument(table);
    assertThat(tableDocument.getPrimaryKey(), is(notNullValue()));
  }
}
