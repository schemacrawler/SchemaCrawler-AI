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

package schemacrawler.tools.command.aichat.functions.json;

import static schemacrawler.tools.command.aichat.options.DatabaseObjectType.ALL;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import schemacrawler.tools.command.aichat.options.DatabaseObjectType;
import schemacrawler.tools.command.aichat.tools.FunctionParameters;
import schemacrawler.tools.command.aichat.utility.JsonUtility;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record ListFunctionParameters(
    @JsonPropertyDescription(
            """
    Type of database object to list, like tables (including views),
    routines (that is, stored procedures and functions),
    schemas (that is, catalogs), sequences, or synonyms.
    If the parameter is not provided, all database objects are listed.
    """)
        @JsonProperty(defaultValue = "ALL", required = false)
        DatabaseObjectType databaseObjectType,
    @JsonPropertyDescription(
            """
    Name of database object to list.
    Is a regular expression, matching the fully qualified
    database object name (including the schema). May match
    more than one database object.
    Use an empty string if all database objects are requested.
    """)
        @JsonProperty(defaultValue = "", required = false)
        String databaseObjectName)
    implements FunctionParameters {

  public ListFunctionParameters {
    if (databaseObjectType == null) {
      databaseObjectType = ALL;
    }
  }

  @Override
  public String toString() {
    return JsonUtility.parametersToString(this);
  }
}
