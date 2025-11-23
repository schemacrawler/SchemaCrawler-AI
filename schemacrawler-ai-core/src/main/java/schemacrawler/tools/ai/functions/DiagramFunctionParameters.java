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
            Try not to match all tables, but instead use a regular expression
            to match a subset or match a single table, since otherwise results may
            be large.
            """)
        @JsonProperty(required = false)
        String tableName,
    @JsonPropertyDescription(
            """
            If true, also include child (or dependent) tables for the selected tables.
            """)
        @JsonProperty(required = false, defaultValue = "false")
        boolean includeChildTables,
    @JsonPropertyDescription(
            """
            If true, also include tables that are referenced by the selected tables.
            (These are sometimes known as parent tables.)
            """)
        @JsonProperty(required = false, defaultValue = "false")
        boolean includeReferencedTables,
    @JsonPropertyDescription(
            """
            Indicates database schema diagram format - Graphviz DOT format, PlantUML,
            Mermaid or DBML from dbdiagram.io.
            """)
        @JsonProperty(required = true)
        DiagramType diagramType)
    implements FunctionParameters {

  public enum DiagramType {
    PLANTUML("script", "text", "/scripts/plantuml.py", "https://editor.plantuml.com/"),
    MERMAID("script", "text", "/scripts/mermaid.py", "https://mermaid.live/"),
    DBML("script", "text", "/scripts/dbml.py", "https://dbdiagram.io/d"),
    GRAPHVIZ("schema", "scdot", "", "https://dreampuf.github.io/GraphvizOnline/"),
    ;

    private final String command;
    private final String outputFormatValue;
    private final String script;
    private final String onlineEditorUrl;

    DiagramType(
        final String command,
        final String outputFormatValue,
        final String script,
        final String url) {
      this.command = command;
      this.outputFormatValue = outputFormatValue;
      this.script = script;
      onlineEditorUrl = url;
    }

    public String getCommand() {
      return command;
    }

    public String getOnlineEditorUrl() {
      return onlineEditorUrl;
    }

    public String getOutputFormatValue() {
      return outputFormatValue;
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
