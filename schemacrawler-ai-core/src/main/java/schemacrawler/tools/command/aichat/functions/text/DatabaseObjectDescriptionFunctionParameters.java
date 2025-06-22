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

package schemacrawler.tools.command.aichat.functions.text;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import schemacrawler.tools.command.aichat.tools.FunctionParameters;
import schemacrawler.tools.command.aichat.utility.JsonUtility;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record DatabaseObjectDescriptionFunctionParameters(
    @JsonPropertyDescription(
            """
    Name of database object to describe.
    Can be a regular expression.
    Use an empty string if all database objects are requested.
    """)
        @JsonProperty(defaultValue = "", required = false)
        String databaseObjectName,
    @JsonPropertyDescription(
            """
        Indicates the type of database objects to show - like
        tables (all types, including views),
        routines (that is, functions and stored procedures),
        sequences, or synonyms.
        """)
        @JsonProperty(defaultValue = "NONE", required = true)
        DatabaseObjectsScope databaseObjectsScope)
    implements FunctionParameters {

  public enum DatabaseObjectsScope {
    NONE,
    TABLES,
    ROUTINES,
    SEQUENCES,
    SYNONYMS;
  }

  public DatabaseObjectDescriptionFunctionParameters {
    if (databaseObjectName == null || databaseObjectName.isBlank()) {
      databaseObjectName = "";
    }
    if (databaseObjectsScope == null) {
      databaseObjectsScope = DatabaseObjectsScope.NONE;
    }
  }

  @Override
  public String toString() {
    return JsonUtility.parametersToString(this);
  }
}
