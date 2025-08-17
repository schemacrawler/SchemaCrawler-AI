/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.model;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.model.AdditionalTableDetails.ATTRIBUTES;
import static schemacrawler.tools.ai.model.AdditionalTableDetails.DEFINIITION;
import static schemacrawler.tools.ai.model.AdditionalTableDetails.INDEXES;
import static schemacrawler.tools.ai.model.AdditionalTableDetails.PRIMARY_KEY;
import static schemacrawler.tools.ai.model.AdditionalTableDetails.REFERENCED_TABLES;
import static schemacrawler.tools.ai.model.AdditionalTableDetails.TRIGGERS;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.utility.MetaDataUtility;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
  "schema",
  "name",
  "type",
  "remarks",
  "columns",
  "primary-key",
  "referenced-tables",
  "indexes",
  "triggers",
  "attributes",
  "definition"
})
public final class TableDocument implements Document {

  private static final long serialVersionUID = 1873929712139211255L;

  private final String schemaName;
  private final String tableName;
  private final String type;
  private final String remarks;
  private final List<ColumnDocument> columns;
  private final IndexDocument primaryKey;
  private final Collection<DatabaseObjectDocument> referencedTables;
  private final Collection<IndexDocument> indexes;
  private final Collection<TriggerDocument> triggers;
  private final Map<String, String> attributes;

  private final String definition;

  TableDocument(final Table table, final Map<AdditionalTableDetails, Boolean> tableDetails) {
    Objects.requireNonNull(table, "No table provided");
    final Map<AdditionalTableDetails, Boolean> details = defaults(tableDetails);

    final String schemaName = table.getSchema().getFullName();
    this.schemaName = trimToEmpty(schemaName);

    tableName = table.getName();
    type = MetaDataUtility.getSimpleTypeName(table).name();

    final Map<String, Column> referencedColumns = mapReferencedColumns(table);
    columns = new ArrayList<>();
    for (final Column column : table.getColumns()) {
      final ColumnDocument columnDocument =
          new ColumnDocument(column, referencedColumns.get(column.getName()));
      columns.add(columnDocument);
    }

    if (details.get(PRIMARY_KEY) && table.hasPrimaryKey()) {
      primaryKey = new IndexDocument(table.getPrimaryKey());
    } else {
      primaryKey = null;
    }

    if (details.get(REFERENCED_TABLES)) {
      final Collection<Table> references = table.getReferencedObjects();
      Collections.sort(new ArrayList<>(references));
      referencedTables = new ArrayList<>();
      for (final Table referencedTable : references) {
        referencedTables.add(new DatabaseObjectDocument(referencedTable));
      }
    } else {
      referencedTables = null;
    }

    if (details.get(INDEXES)) {
      final Collection<Index> tableIndexes = table.getIndexes();
      Collections.sort(new ArrayList<>(tableIndexes));
      indexes = new ArrayList<>();
      for (final Index index : tableIndexes) {
        indexes.add(new IndexDocument(index));
      }
    } else {
      indexes = null;
    }

    if (table.hasRemarks()) {
      final String remarks = table.getRemarks();
      this.remarks = trimToEmpty(remarks);
    } else {
      remarks = null;
    }

    if (details.get(TRIGGERS) && table.hasTriggers()) {
      final Collection<Trigger> tableTriggers = table.getTriggers();
      Collections.sort(new ArrayList<>(tableTriggers));
      triggers = new ArrayList<>();
      for (final Trigger trigger : tableTriggers) {
        triggers.add(new TriggerDocument(trigger));
      }
    } else {
      triggers = null;
    }

    if (details.get(DEFINIITION) && table.hasDefinition()) {
      definition = table.getDefinition();
    } else {
      definition = null;
    }

    if (details.get(ATTRIBUTES)) {
      attributes = new HashMap<>();
      table.getAttributes().entrySet().stream()
          .filter(entry -> entry.getValue() != null && !isBlank(entry.getValue().toString()))
          .forEach(entry -> attributes.put(entry.getKey(), String.valueOf(entry.getValue())));
    } else {
      attributes = null;
    }
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public List<ColumnDocument> getColumns() {
    return columns;
  }

  public String getDefinition() {
    return definition;
  }

  public Collection<IndexDocument> getIndexes() {
    return indexes;
  }

  @Override
  public String getName() {
    return tableName;
  }

  public IndexDocument getPrimaryKey() {
    return primaryKey;
  }

  /**
   * For tables, these are child tables, and for views, they are "table usage".
   *
   * @return Referenced tables
   */
  public Collection<DatabaseObjectDocument> getReferencedTables() {
    return referencedTables;
  }

  public String getRemarks() {
    return remarks;
  }

  @JsonProperty("schema")
  public String getSchemaName() {
    return schemaName;
  }

  public Collection<TriggerDocument> getTriggers() {
    return triggers;
  }

  public String getType() {
    return type;
  }

  @Override
  public ObjectNode toObjectNode() {
    return mapper.valueToTree(this);
  }

  @Override
  public String toString() {
    return toObjectNode().toString();
  }

  private Map<AdditionalTableDetails, Boolean> defaults(
      final Map<AdditionalTableDetails, Boolean> tableDetails) {
    final Map<AdditionalTableDetails, Boolean> details;
    if (tableDetails == null) {
      details = new EnumMap<>(AdditionalTableDetails.class);
    } else {
      details = tableDetails;
    }

    for (final AdditionalTableDetails additionalTableDetails : AdditionalTableDetails.values()) {
      if (!details.containsKey(additionalTableDetails)) {
        details.put(additionalTableDetails, false);
      }
    }
    return details;
  }

  private Map<String, Column> mapReferencedColumns(final Table table) {
    requireNonNull(table, "No table provided");

    final Map<String, Column> referencedColumns = new HashMap<>();
    for (final ForeignKey foreignKey : table.getImportedForeignKeys()) {
      final List<ColumnReference> columnReferences = foreignKey.getColumnReferences();
      for (final ColumnReference columnReference : columnReferences) {
        referencedColumns.put(
            columnReference.getForeignKeyColumn().getName(), columnReference.getPrimaryKeyColumn());
      }
    }
    return referencedColumns;
  }
}
