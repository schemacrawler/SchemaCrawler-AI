/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.chatgpt.functions;

import java.util.Optional;
import java.util.function.Supplier;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.command.chatgpt.FunctionReturn;
import schemacrawler.utility.MetaDataUtility;

public final class TableReferencesFunctionDefinition extends AbstractFunctionDefinition {

  public enum TableReferenceType {
    ALL,
    PARENT,
    CHILD;
  }

  @JsonPropertyDescription("Name of database table for which to show references.")
  @JsonProperty(required = true)
  private String tableName;

  @JsonPropertyDescription(
      "The type of related tables requested - either child tables or parent tables, or both types (all relationships).")
  private TableReferenceType tableReferenceType;

  @Override
  public String getDescription() {
    return "Gets the relationships of a database table, either child tables or parent tables. "
        + "Child tables are also known as dependent tables or foreign key tables. "
        + "Parent tables are also known as referenced tables, or primary key tables.";
  }

  @JsonIgnore
  @Override
  public Supplier<FunctionReturn> getExecutor() {
    return () -> {
      // Re-filter catalog
      MetaDataUtility.reduceCatalog(catalog, SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions());

      final Optional<Table> firstMatchedTable =
          catalog.getTables().stream()
              .filter(table -> table.getName().matches("(?i)" + getTableName()))
              .findFirst();

      if (firstMatchedTable.isPresent()) {
        final Table table = firstMatchedTable.get();
        return new TableReferencesFunctionReturn(table, getTableReferenceType());
      }
      return new NoResultsReturn();
    };
  }

  public String getTableName() {
    return tableName;
  }

  public TableReferenceType getTableReferenceType() {
    if (tableReferenceType == null) {
      return TableReferenceType.ALL;
    }
    return tableReferenceType;
  }

  public void setTableName(final String tableName) {
    this.tableName = tableName;
  }

  public void setTableReferenceType(final TableReferenceType tableReferenceType) {
    this.tableReferenceType = tableReferenceType;
  }
}
