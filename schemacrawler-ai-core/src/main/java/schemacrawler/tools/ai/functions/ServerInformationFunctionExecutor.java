/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.functions;

import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import java.util.Collection;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.ai.tools.JsonFunctionReturn;
import schemacrawler.tools.ai.tools.NoParameters;
import schemacrawler.tools.ai.tools.base.AbstractJsonFunctionExecutor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import us.fatehi.utility.property.Property;
import us.fatehi.utility.property.PropertyName;

public final class ServerInformationFunctionExecutor
    extends AbstractJsonFunctionExecutor<NoParameters> {

  protected ServerInformationFunctionExecutor(final PropertyName functionName) {
    super(functionName);
  }

  @Override
  public JsonFunctionReturn call() throws Exception {
    // No need to refilter, but leave this boilerplate
    // refilterCatalog();

    final JsonNode serverInfo = createServerInfoArray();
    return new JsonFunctionReturn(serverInfo);
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions() {
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
  }

  private JsonNode createServerInfoArray() {

    final ObjectNode databaseInfo = mapper.createObjectNode();

    final ObjectNode databaseProductPropertyNode = databaseInfo.putObject("database-server");
    databaseProductPropertyNode.put(
        "database-product-name", catalog.getDatabaseInfo().getDatabaseProductName());
    databaseProductPropertyNode.put(
        "database-product-version", catalog.getDatabaseInfo().getDatabaseProductVersion());

    final ArrayNode serverInfoArray = databaseInfo.putArray("server-info");
    final Collection<Property> serverInfo = catalog.getDatabaseInfo().getServerInfo();
    for (final Property serverProperty : serverInfo) {
      if (serverProperty == null || serverProperty.getValue() == null) {
        continue;
      }
      final ObjectNode serverPropertyNode = serverInfoArray.addObject();
      serverPropertyNode.put("name", serverProperty.getName());
      serverPropertyNode.put("description", serverProperty.getDescription());
      serverPropertyNode.put("value", serverProperty.getValue().toString());
    }

    return databaseInfo;
  }
}
