/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.model;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serial;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.TypedObject;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.node.ObjectNode;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"full_name", "schema", "name", "type"})
public class BaseObjectDocument implements Document {

  @Serial private static final long serialVersionUID = -6765691827862270251L;

  private final String fullName;
  private final String schema;
  private final String name;
  private final String type;

  public BaseObjectDocument(final NamedObject namedObject) {
    requireNonNull(namedObject, "No named object provided");

    fullName = namedObject.getFullName();

    if (namedObject instanceof final DatabaseObject databaseObject) {
      final String schemaName = databaseObject.getSchema().getFullName();
      if (isBlank(schemaName)) {
        schema = null;
      } else {
        schema = trimToEmpty(schemaName);
      }
    } else {
      schema = null;
    }

    name = namedObject.getName();

    type =
        switch (namedObject) {
          case final TypedObject<?> typedObject -> typedObject.getType().toString().toLowerCase();
          case final Synonym synonym -> "synonym";
          case final Sequence sequence -> "sequence";
          case final Schema schema -> "schema";
          default -> null;
        };
  }

  public String getFullName() {
    return fullName;
  }

  @Override
  public String getName() {
    return name;
  }

  public String getSchema() {
    return schema;
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
}
