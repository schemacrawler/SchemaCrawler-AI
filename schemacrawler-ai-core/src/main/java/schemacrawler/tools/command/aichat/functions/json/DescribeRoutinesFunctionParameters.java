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

import java.util.ArrayList;
import java.util.Collection;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import schemacrawler.tools.command.aichat.tools.FunctionParameters;
import schemacrawler.tools.command.aichat.utility.JsonUtility;
import schemacrawler.tools.command.serialize.model.AdditionalRoutineDetails;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record DescribeRoutinesFunctionParameters(
    @JsonPropertyDescription(
            """
    Name of database routine (stored procedure or function) to describe.
    May be specified as a regular expression matching the fully qualified
    stored procedure or function names (including the schema).
    Use an empty string if all routines are requested.
    If not specified, all routines are returned, but the results
    could be large.
    """)
        @JsonProperty(required = false)
        String routineName,
    @JsonPropertyDescription(
            """
    Indicates what details of the database stored procedure or function
    to return - parameters (including return types), attributes,
    and routine definition.
    Parameters, return types, and remarks or comments are always returned.
    """)
        @JsonProperty(required = false)
        Collection<RoutineDescriptionScope> descriptionScope)
    implements FunctionParameters {

  public enum RoutineDescriptionScope {
    DEFAULT(null),
    ATTRIBUTES(AdditionalRoutineDetails.ATTRIBUTES),
    DEFINIITION(AdditionalRoutineDetails.DEFINIITION);

    private final AdditionalRoutineDetails additionalRoutineDetails;

    RoutineDescriptionScope(final AdditionalRoutineDetails additionalRoutineDetails) {
      this.additionalRoutineDetails = additionalRoutineDetails;
    }

    public AdditionalRoutineDetails toAdditionalRoutineDetails() {
      return additionalRoutineDetails;
    }
  }

  public DescribeRoutinesFunctionParameters {
    if (routineName == null || routineName.isBlank()) {
      routineName = "";
    }
    if (descriptionScope == null) {
      descriptionScope = new ArrayList<>();
    }
  }

  @Override
  public String toString() {
    return JsonUtility.parametersToString(this);
  }
}
