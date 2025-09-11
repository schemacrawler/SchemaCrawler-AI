/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver;

import java.util.Collection;
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

  private final String errorMessage;

  public EmptyCatalog(final Exception e) {
    final String baseErrorMessage =
        """
        The SchemaCrawler AI MCP server is in an error state.
        Database schema metadata is not available,
        since it could not make a connection to the database.
        Correct the error, and restart the server.
        """
            .strip()
            .trim();
    if (e != null) {
      errorMessage = baseErrorMessage + "\n" + e.getMessage();
    } else {
      errorMessage = baseErrorMessage;
    }
  }

  @Override
  public int compareTo(final NamedObject o) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public <T> T getAttribute(final String name) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public <T> T getAttribute(final String name, final T defaultValue) throws ClassCastException {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public Map<String, Object> getAttributes() {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public Collection<ColumnDataType> getColumnDataTypes() {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public Collection<ColumnDataType> getColumnDataTypes(final Schema schema) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public CrawlInfo getCrawlInfo() {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public DatabaseInfo getDatabaseInfo() {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public Collection<DatabaseUser> getDatabaseUsers() {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public String getFullName() {
    return getName();
  }

  @Override
  public JdbcDriverInfo getJdbcDriverInfo() {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public String getName() {
    return "empty-catalog";
  }

  @Override
  public String getRemarks() {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public Collection<Routine> getRoutines() {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public Collection<Routine> getRoutines(final Schema schema) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public Collection<Routine> getRoutines(final Schema schema, final String routineName) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public Collection<Schema> getSchemas() {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public Collection<Sequence> getSequences() {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public Collection<Sequence> getSequences(final Schema schema) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public Collection<Synonym> getSynonyms() {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public Collection<Synonym> getSynonyms(final Schema schema) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public Collection<ColumnDataType> getSystemColumnDataTypes() {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public Collection<Table> getTables() {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public Collection<Table> getTables(final Schema schema) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public boolean hasAttribute(final String name) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public boolean hasRemarks() {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public NamedObjectKey key() {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public <T> Optional<T> lookupAttribute(final String name) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public Optional<Column> lookupColumn(
      final Schema schema, final String tableName, final String name) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public <C extends ColumnDataType> Optional<C> lookupColumnDataType(
      final Schema schema, final String dataTypeName) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public <S extends Schema> Optional<S> lookupSchema(final String name) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public <S extends Sequence> Optional<S> lookupSequence(
      final Schema schema, final String sequenceName) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public <S extends Synonym> Optional<S> lookupSynonym(
      final Schema schema, final String synonymName) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public <C extends ColumnDataType> Optional<C> lookupSystemColumnDataType(final String name) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public <T extends Table> Optional<T> lookupTable(final Schema schema, final String tableName) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public <N extends NamedObject> void reduce(final Class<N> clazz, final Reducer<N> reducer) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public void removeAttribute(final String name) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public <T> void setAttribute(final String name, final T value) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public void setRemarks(final String remarks) {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public <N extends NamedObject> void undo(final Class<N> clazz, final Reducer<N> reducer) {
    throw new UnsupportedOperationException(errorMessage);
  }
}
