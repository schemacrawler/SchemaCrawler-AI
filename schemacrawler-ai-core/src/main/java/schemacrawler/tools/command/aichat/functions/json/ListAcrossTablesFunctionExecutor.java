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
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.DependantObject;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.command.aichat.functions.json.ListAcrossTablesFunctionParameters.DependantObjectType;
import schemacrawler.tools.command.aichat.tools.FunctionReturn;
import us.fatehi.utility.property.PropertyName;

public final class ListAcrossTablesFunctionExecutor
    extends AbstractJsonFunctionExecutor<ListAcrossTablesFunctionParameters> {

  protected ListAcrossTablesFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public FunctionReturn call() throws Exception {
    refilterCatalog();

    final Collection<DependantObject<Table>> dependantObjects = new ArrayList<>();
    final DependantObjectType dependantObjectType = commandOptions.dependantObjectType();

    for (final Table table : catalog.getTables()) {
      switch (dependantObjectType) {
        case COLUMNS:
          dependantObjects.addAll(table.getColumns());
          break;
        case INDEXES:
          dependantObjects.addAll(table.getIndexes());
          break;
        case FOREIGN_KEYS:
          dependantObjects.addAll(table.getForeignKeys());
          break;
        case TRIGGERS:
          dependantObjects.addAll(table.getTriggers());
          break;
        default:
          break;
      }
    }

    return () -> createTypedObjectsArray(dependantObjects, dependantObjectType).toString();
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {

    final DependantObjectType dependantObjectType = commandOptions.dependantObjectType();
    final Pattern grepColumnsPattern =
        makeNameInclusionPattern(commandOptions.dependantObjectName());

    final Pattern grepTablesPattern = makeNameInclusionPattern(commandOptions.tableName());

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSynonyms(new ExcludeAll())
            .includeSequences(new ExcludeAll())
            .includeRoutines(new ExcludeAll());
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder().includeGreppedTables(grepTablesPattern);
    if (dependantObjectType == DependantObjectType.COLUMNS) {
      grepOptionsBuilder.includeGreppedColumns(grepColumnsPattern);
    }
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLimitOptions(limitOptionsBuilder.toOptions())
        .withGrepOptions(grepOptionsBuilder.toOptions());
  }

  private ArrayNode createTypedObjectsArray(
      final Collection<DependantObject<Table>> dependantObjects,
      final DependantObjectType dependantObjectType) {

    final InclusionRule inclusionRule = makeDependantObjectInclusionRule();

    final ObjectMapper mapper = new ObjectMapper();
    final ArrayNode list = mapper.createArrayNode();
    if (dependantObjects == null || dependantObjects.isEmpty()) {
      return list;
    }

    final String nameAttribute = dependantObjectType.nameAttribute();

    for (final DependantObject<Table> dependantObject : dependantObjects) {
      if (dependantObject == null || !inclusionRule.test(dependantObject.getFullName())) {
        continue;
      }

      final ObjectNode objectNode = mapper.createObjectNode();
      final String schemaName = dependantObject.getSchema().getFullName();
      if (!isBlank(schemaName)) {
        objectNode.put("schema", schemaName);
      }
      objectNode.put("table", dependantObject.getParent().getName());
      objectNode.put(nameAttribute, dependantObject.getName());

      list.add(objectNode);
    }

    return list;
  }

  private InclusionRule makeDependantObjectInclusionRule() {
    final InclusionRule inclusionRule;
    if (isBlank(commandOptions.dependantObjectName())) {
      inclusionRule = new IncludeAll();
    } else {
      final Pattern dependantObjectPattern =
          makeNameInclusionPattern(commandOptions.dependantObjectName());
      inclusionRule = new RegularExpressionInclusionRule(dependantObjectPattern);
    }
    return inclusionRule;
  }
}
