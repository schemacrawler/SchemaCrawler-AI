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

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import schemacrawler.tools.command.chatgpt.FunctionParameters;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public class TableDecriptionFunctionParameters implements FunctionParameters {

  public enum TableDescriptionScope {
    DEFAULT, COLUMNS, PRIMARY_KEY, INDEXES, FOREIGN_KEYS, TRIGGERS;
  }

  @JsonPropertyDescription("Name of database table or view to describe.")
  private String tableName;

  @JsonPropertyDescription("Indicates what details of the database table or view to show - columns, primary key, indexes, foreign keys, or triggers.")
  private TableDescriptionScope descriptionScope;

  public TableDescriptionScope getDescriptionScope() {
    if (descriptionScope == null) {
      return TableDescriptionScope.DEFAULT;
    }
    return descriptionScope;
  }

  public String getTableName() {
    return tableName;
  }

  public void setDescriptionScope(final TableDescriptionScope descriptionScope) {
    this.descriptionScope = descriptionScope;
  }

  public void setTableName(final String tableName) {
    this.tableName = tableName;
  }

  @Override
  public String toString() {
    try {
      return new ObjectMapper().writeValueAsString(this);
    } catch (final JsonProcessingException e) {
      return super.toString();
    }
  }
}
