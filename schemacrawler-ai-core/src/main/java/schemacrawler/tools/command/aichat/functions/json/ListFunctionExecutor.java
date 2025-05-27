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

import static schemacrawler.tools.command.aichat.functions.json.ListFunctionParameters.ListType.ALL;
import static schemacrawler.tools.command.aichat.functions.json.ListFunctionParameters.ListType.ROUTINES;
import static schemacrawler.tools.command.aichat.functions.json.ListFunctionParameters.ListType.SEQUENCES;
import static schemacrawler.tools.command.aichat.functions.json.ListFunctionParameters.ListType.SYNONYMS;
import static schemacrawler.tools.command.aichat.functions.json.ListFunctionParameters.ListType.TABLES;
import java.util.ArrayList;
import java.util.Collection;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.TypedObject;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.command.aichat.functions.json.ListFunctionParameters.ListType;
import schemacrawler.tools.command.aichat.tools.FunctionReturn;
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
    final ListType listType = commandOptions.listType();
    if (listType == TABLES || listType == ALL) {
      databaseObjects.addAll(catalog.getTables());
    } // fall through - no else
    if (listType == ROUTINES || listType == ALL) {
      databaseObjects.addAll(catalog.getRoutines());
    } // fall through - no else
    if (listType == SEQUENCES || listType == ALL) {
      databaseObjects.addAll(catalog.getSequences());
    } // fall through - no else
    if (listType == SYNONYMS || listType == ALL) {
      databaseObjects.addAll(catalog.getSynonyms());
    } // fall through - no else

    return () -> createTypedObjectsArray(databaseObjects).toString();
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
    final ListType listType = commandOptions.listType();
    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    if (listType != TABLES && listType != ALL) {
      limitOptionsBuilder.includeTables(new ExcludeAll());
    } // fall through - no else
    if (listType != ROUTINES && listType == ALL) {
      limitOptionsBuilder.includeAllRoutines();
    } // fall through - no else
    if (listType == SEQUENCES || listType == ALL) {
      limitOptionsBuilder.includeAllSequences();
    } // fall through - no else
    if (listType == SYNONYMS || listType == ALL) {
      limitOptionsBuilder.includeAllSynonyms();
    } // fall through - no else

    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLimitOptions(limitOptionsBuilder.toOptions());
  }

  private ArrayNode createTypedObjectsArray(final Collection<DatabaseObject> databaseObjects) {
    final ObjectMapper mapper = new ObjectMapper();
    final ArrayNode list = mapper.createArrayNode();
    if (databaseObjects == null || databaseObjects.isEmpty()) {
      return list;
    }

    for (final DatabaseObject databaseObject : databaseObjects) {
      if (databaseObject == null) {
        continue;
      }
      final ObjectNode objectNode = mapper.createObjectNode();
      objectNode.put("schema", databaseObject.getSchema().getFullName());
      if (databaseObject instanceof TypedObject typedObject) {
        objectNode.put("type", typedObject.getType().toString());
      } else {
        if (databaseObject instanceof Sequence sequence) {
          objectNode.put("type", "sequence");
        }
        if (databaseObject instanceof Synonym synonym) {
          objectNode.put("type", "synonym");
        }
      }
      list.add(objectNode);
    }

    return list;
  }
}
