/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import static schemacrawler.tools.ai.model.DatabaseObjectType.ALL;
import static schemacrawler.tools.ai.model.DatabaseObjectType.ROUTINES;
import static schemacrawler.tools.ai.model.DatabaseObjectType.SEQUENCES;
import static schemacrawler.tools.ai.model.DatabaseObjectType.SYNONYMS;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;
import static us.fatehi.utility.Utility.isBlank;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collection;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.TypedObject;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.ai.model.DatabaseObjectType;
import schemacrawler.tools.ai.tools.FunctionReturn;
import us.fatehi.utility.property.PropertyName;

public final class ListFunctionExecutor
    extends AbstractJsonFunctionExecutor<ListFunctionParameters> {

  protected ListFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public FunctionReturn call() throws Exception {
    refilterCatalog();

    final Collection<DatabaseObject> databaseObjects = new ArrayList<>();
    final DatabaseObjectType databaseObjectType = commandOptions.databaseObjectType();
    if (databaseObjectType == DatabaseObjectType.TABLES || databaseObjectType == ALL) {
      databaseObjects.addAll(catalog.getTables());
    } // fall through - no else
    if (databaseObjectType == ROUTINES || databaseObjectType == ALL) {
      databaseObjects.addAll(catalog.getRoutines());
    } // fall through - no else
    if (databaseObjectType == SEQUENCES || databaseObjectType == ALL) {
      databaseObjects.addAll(catalog.getSequences());
    } // fall through - no else
    if (databaseObjectType == SYNONYMS || databaseObjectType == ALL) {
      databaseObjects.addAll(catalog.getSynonyms());
    } // fall through - no else

    return () -> {
      final ArrayNode list = createTypedObjectsArray(databaseObjects);
      final ObjectNode listObject = wrapList(list);
      return listObject.toString();
    };
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
    final InclusionRule databaseObjectPattern =
        makeInclusionRule(commandOptions.databaseObjectName());
    final DatabaseObjectType databaseObjectType = commandOptions.databaseObjectType();
    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    if (databaseObjectType == DatabaseObjectType.TABLES || databaseObjectType == ALL) {
      limitOptionsBuilder.includeTables(databaseObjectPattern);
    } // fall through - no else
    if (databaseObjectType == ROUTINES || databaseObjectType == ALL) {
      limitOptionsBuilder.includeRoutines(databaseObjectPattern);
    } // fall through - no else
    if (databaseObjectType == SEQUENCES || databaseObjectType == ALL) {
      limitOptionsBuilder.includeSequences(databaseObjectPattern);
    } // fall through - no else
    if (databaseObjectType == SYNONYMS || databaseObjectType == ALL) {
      limitOptionsBuilder.includeSynonyms(databaseObjectPattern);
    } // fall through - no else

    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLimitOptions(limitOptionsBuilder.toOptions());
  }

  private ArrayNode createTypedObjectsArray(final Collection<DatabaseObject> databaseObjects) {
    final ArrayNode list = mapper.createArrayNode();
    if (databaseObjects == null || databaseObjects.isEmpty()) {
      return list;
    }

    for (final DatabaseObject databaseObject : databaseObjects) {
      if (databaseObject == null) {
        continue;
      }
      final ObjectNode objectNode = mapper.createObjectNode();
      final String schemaName = databaseObject.getSchema().getFullName();
      if (!isBlank(schemaName)) {
        objectNode.put("schema", schemaName);
      }
      objectNode.put("name", databaseObject.getName());
      if (databaseObject instanceof TypedObject typedObject) {
        objectNode.put("type", typedObject.getType().toString());
      } else {
        if (databaseObject instanceof Sequence) {
          objectNode.put("type", "sequence");
        }
        if (databaseObject instanceof Synonym) {
          objectNode.put("type", "synonym");
        }
      }
      list.add(objectNode);
    }

    return list;
  }
}
