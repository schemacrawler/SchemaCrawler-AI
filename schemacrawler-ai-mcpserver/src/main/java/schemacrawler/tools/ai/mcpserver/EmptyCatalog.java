/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.DatabaseUser;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Reducer;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;

public class EmptyCatalog implements Catalog {

  private static final long serialVersionUID = -8018517276190501450L;

  @Override
  public int compareTo(final NamedObject o) {
    return -1;
  }

  @Override
  public <T> T getAttribute(final String name) {
    return null;
  }

  @Override
  public <T> T getAttribute(final String name, final T defaultValue) throws ClassCastException {
    return null;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return Collections.emptyMap();
  }

  @Override
  public Collection<ColumnDataType> getColumnDataTypes() {
    return Collections.emptyList();
  }

  @Override
  public Collection<ColumnDataType> getColumnDataTypes(final Schema schema) {
    return Collections.emptyList();
  }

  @Override
  public CrawlInfo getCrawlInfo() {
    throw new UnsupportedOperationException("No supported in an empty catalog");
  }

  @Override
  public DatabaseInfo getDatabaseInfo() {
    throw new UnsupportedOperationException("No supported in an empty catalog");
  }

  @Override
  public Collection<DatabaseUser> getDatabaseUsers() {
    return Collections.emptyList();
  }

  @Override
  public String getFullName() {
    return getName();
  }

  @Override
  public JdbcDriverInfo getJdbcDriverInfo() {
    throw new UnsupportedOperationException("No supported in an empty catalog");
  }

  @Override
  public String getName() {
    return "empty-catalog";
  }

  @Override
  public String getRemarks() {
    return getName();
  }

  @Override
  public Collection<Routine> getRoutines() {
    return Collections.emptyList();
  }

  @Override
  public Collection<Routine> getRoutines(final Schema schema) {
    return Collections.emptyList();
  }

  @Override
  public Collection<Routine> getRoutines(final Schema schema, final String routineName) {
    return Collections.emptyList();
  }

  @Override
  public Collection<Schema> getSchemas() {
    return Collections.emptyList();
  }

  @Override
  public Collection<Sequence> getSequences() {
    return Collections.emptyList();
  }

  @Override
  public Collection<Sequence> getSequences(final Schema schema) {
    return Collections.emptyList();
  }

  @Override
  public Collection<Synonym> getSynonyms() {
    return Collections.emptyList();
  }

  @Override
  public Collection<Synonym> getSynonyms(final Schema schema) {
    return Collections.emptyList();
  }

  @Override
  public Collection<ColumnDataType> getSystemColumnDataTypes() {
    return Collections.emptyList();
  }

  @Override
  public Collection<Table> getTables() {
    return Collections.emptyList();
  }

  @Override
  public Collection<Table> getTables(final Schema schema) {
    return Collections.emptyList();
  }

  @Override
  public boolean hasAttribute(final String name) {
    return false;
  }

  @Override
  public boolean hasRemarks() {
    return false;
  }

  @Override
  public NamedObjectKey key() {
    return new NamedObjectKey((String) null);
  }

  @Override
  public <T> Optional<T> lookupAttribute(final String name) {
    return Optional.empty();
  }

  @Override
  public Optional<Column> lookupColumn(
      final Schema schema, final String tableName, final String name) {
    return Optional.empty();
  }

  @Override
  public <C extends ColumnDataType> Optional<C> lookupColumnDataType(
      final Schema schema, final String dataTypeName) {
    return Optional.empty();
  }

  @Override
  public <S extends Schema> Optional<S> lookupSchema(final String name) {
    return Optional.empty();
  }

  @Override
  public <S extends Sequence> Optional<S> lookupSequence(
      final Schema schema, final String sequenceName) {
    return Optional.empty();
  }

  @Override
  public <S extends Synonym> Optional<S> lookupSynonym(
      final Schema schema, final String synonymName) {
    return Optional.empty();
  }

  @Override
  public <C extends ColumnDataType> Optional<C> lookupSystemColumnDataType(final String name) {
    return Optional.empty();
  }

  @Override
  public <T extends Table> Optional<T> lookupTable(final Schema schema, final String tableName) {
    return Optional.empty();
  }

  @Override
  public <N extends NamedObject> void reduce(final Class<N> clazz, final Reducer<N> reducer) {
    // No-op
  }

  @Override
  public void removeAttribute(final String name) {
    // No-op
  }

  @Override
  public <T> void setAttribute(final String name, final T value) {
    // No-op
  }

  @Override
  public void setRemarks(final String remarks) {
    // No-op
  }

  @Override
  public <N extends NamedObject> void undo(final Class<N> clazz, final Reducer<N> reducer) {
    // No-op
  }
}
