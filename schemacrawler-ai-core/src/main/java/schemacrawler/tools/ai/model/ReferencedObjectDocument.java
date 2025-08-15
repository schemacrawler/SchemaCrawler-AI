/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.utility.MetaDataUtility;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"schema", "name", "type"})
public final class ReferencedObjectDocument implements Serializable {

  private static final long serialVersionUID = -2159895984317222363L;

  private final String schemaName;
  private final String name;
  private final String type;

  public ReferencedObjectDocument(final DatabaseObject databaseObject) {
    requireNonNull(databaseObject, "No database object provided");

    final String schema = databaseObject.getSchema().getFullName();
    if (!isBlank(schema)) {
      schemaName = schema;
    } else {
      schemaName = null;
    }
    name = databaseObject.getName();

    type = MetaDataUtility.getSimpleTypeName(databaseObject).name();
  }

  @JsonProperty("name")
  public String getObjectName() {
    return name;
  }

  @JsonProperty("schema")
  public String getSchemaName() {
    return schemaName;
  }
}
