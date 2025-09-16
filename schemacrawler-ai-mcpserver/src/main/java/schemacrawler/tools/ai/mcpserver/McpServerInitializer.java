/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.ai.mcpserver;

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.ai.mcpserver.server.ConfigurationManager;
import schemacrawler.tools.ai.mcpserver.server.ConnectionService;
import schemacrawler.tools.command.mcpserver.McpServerTransportType;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

public class McpServerInitializer
    implements ApplicationContextInitializer<GenericApplicationContext> {

  private static final Logger LOGGER = Logger.getLogger(McpServerInitializer.class.getName());

  @NonNull
  private static Catalog getCatalog(
      final McpServerContext context, final DatabaseConnectionSource connectionSource) {
    requireNonNull(context, "No context provided");
    try {
      final SchemaCrawlerOptions schemaCrawlerOptions = context.getSchemaCrawlerOptions();
      final Catalog catalog =
          SchemaCrawlerUtility.getCatalog(connectionSource, schemaCrawlerOptions);
      return catalog;
    } catch (final Exception e) {
      LOGGER.log(Level.SEVERE, "Could not load catalog", e);
      if (context.mcpTransport() != McpServerTransportType.stdio) {
        throw new SchemaCrawlerException("Could not obtain database metadata", e);
      }
      return new EmptyCatalog(e);
    }
  }

  private final Catalog catalog;
  private final DatabaseConnectionSource connectionSource;
  private final McpServerTransportType mcpTransport;

  public McpServerInitializer(
      final Catalog catalog,
      final Connection connection,
      final McpServerTransportType mcpTransport) {
    this.catalog = requireNonNull(catalog, "No catalog provided");
    connectionSource = DatabaseConnectionSources.fromConnection(connection);
    this.mcpTransport = requireNonNull(mcpTransport, "No MCP Server transport provided");
  }

  public McpServerInitializer(final McpServerContext context) {
    requireNonNull(context, "No context provided");
    mcpTransport = context.mcpTransport();
    connectionSource = context.buildCatalogDatabaseConnectionSource();
    catalog = getCatalog(context, connectionSource);
  }

  @Override
  public void initialize(@NonNull final GenericApplicationContext context) {

    ConnectionService.instantiate(connectionSource);
    ConfigurationManager.instantiate(mcpTransport, catalog);

    context.registerBean("catalog", Catalog.class, () -> catalog);
    context.registerBean(
        "databaseConnectionSource", DatabaseConnectionSource.class, () -> connectionSource);
    context.registerBean("mcpTransport", McpServerTransportType.class, () -> mcpTransport);
  }
}
