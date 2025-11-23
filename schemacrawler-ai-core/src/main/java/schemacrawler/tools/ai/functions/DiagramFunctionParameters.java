/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.base.ParameterUtility;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record DiagramFunctionParameters(
    @JsonPropertyDescription(
            """
            Name of database table or view to describe.
            May be specified as a regular expression, matching the fully qualified
            table name (including the schema).
            Do not use a regular expression that will match all tables, but instead
            try to match a subset, since otherwise results may be large.
            """)
        @JsonProperty(required = false)
        String tableName,
    @JsonPropertyDescription(
            """
            Also include children (or dependent) tables for the selected tables.
            """)
        @JsonProperty(required = false, defaultValue = "false")
        boolean includeChildren,
    @JsonPropertyDescription(
            """
            Also include parent tables for (or tables that are referenced by) the selected tables.
            """)
        @JsonProperty(required = false, defaultValue = "false")
        boolean includeParents,
    @JsonPropertyDescription(
            """
            Indicates database schema diagram format - Graphviz, PlantUML, Mermaid
            or DBML from dbdiagram.io.
            """)
        @JsonProperty(required = true)
        DiagramType diagramType)
    implements FunctionParameters {

  public enum DiagramType {
    PLANTUML("/scripts/plantuml.py", "https://editor.plantuml.com/"),
    MERMAID("/scripts/mermaid.py", "https://mermaid.live/"),
    DBML("/scripts/dbml.py", "https://dbdiagram.io/d"),
    ;

    private final String script;
    private final String onlineEditorUrl;

    DiagramType(final String script, final String url) {
      this.script = script;
      onlineEditorUrl = url;
    }

    public String getOnlineEditorUrl() {
      return onlineEditorUrl;
    }

    public String script() {
      return script;
    }
  }

  public DiagramFunctionParameters {
    if (tableName == null || tableName.isBlank()) {
      tableName = "";
    }
    if (diagramType == null) {
      diagramType = DiagramType.PLANTUML;
    }
  }

  @Override
  public String toString() {
    return ParameterUtility.parametersToString(this);
  }
}
