/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Collection;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.FunctionReturnType;
import schemacrawler.tools.ai.tools.NoParameters;
import us.fatehi.utility.property.Property;
import us.fatehi.utility.property.PropertyName;

public final class ServerInformationFunctionExecutor
    extends AbstractJsonFunctionExecutor<NoParameters> {

  protected ServerInformationFunctionExecutor(
      final PropertyName functionName, final FunctionReturnType returnType) {
    super(functionName, returnType);
  }

  @Override
  public FunctionReturn call() throws Exception {
    // No need to refilter, but leave this boilerplate
    // refilterCatalog();

    final ArrayNode list = createServerInfoArray();
    return new JsonFunctionReturn("server-information", list);
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
  }

  private ArrayNode createServerInfoArray() {
    final ArrayNode list = mapper.createArrayNode();

    final ObjectNode databaseProductPropertyNode = mapper.createObjectNode();
    databaseProductPropertyNode.put(
        "database-product-name", catalog.getDatabaseInfo().getDatabaseProductName());
    databaseProductPropertyNode.put(
        "database-product-version", catalog.getDatabaseInfo().getDatabaseProductVersion());
    list.add(databaseProductPropertyNode);

    final Collection<Property> serverInfo = catalog.getDatabaseInfo().getServerInfo();
    for (final Property serverProperty : serverInfo) {
      if (serverProperty == null) {
        continue;
      }
      final ObjectNode serverPropertyNode = mapper.createObjectNode();
      serverPropertyNode.put("name", serverProperty.getName());
      serverPropertyNode.put("description", serverProperty.getDescription());
      serverPropertyNode.put("value", serverProperty.getValue().toString());
      list.add(serverPropertyNode);
    }

    return list;
  }
}
