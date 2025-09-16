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
    this.mcpTransport = requireNonNull(mcpTransport, "No MCP Server transport provided");

    DatabaseConnectionSource connectionSource;
    try {
      connectionSource = DatabaseConnectionSources.fromConnection(connection);
    } catch (final Exception e) {
      if (mcpTransport != McpServerTransportType.stdio) {
        throw e;
      }
      connectionSource = new EmptyDatabaseConnectionSource();
    }
    this.connectionSource = connectionSource;

    this.catalog = requireNonNull(catalog, "No catalog provided");
  }

  public McpServerInitializer(final McpServerContext context) {
    requireNonNull(context, "No context provided");

    mcpTransport = context.mcpTransport();

    DatabaseConnectionSource connectionSource;
    Catalog catalog;
    try {
      connectionSource = context.buildCatalogDatabaseConnectionSource();
      catalog = getCatalog(context, connectionSource);
    } catch (final Exception e) {
      if (mcpTransport != McpServerTransportType.stdio) {
        throw e;
      }
      connectionSource = new EmptyDatabaseConnectionSource();
      catalog = new EmptyCatalog(e);
    }

    this.connectionSource = connectionSource;
    this.catalog = catalog;
  }

  @Override
  public void initialize(@NonNull final GenericApplicationContext context) {

    context.registerBean("catalog", Catalog.class, () -> catalog);
    context.registerBean(
        "databaseConnectionSource", DatabaseConnectionSource.class, () -> connectionSource);
    context.registerBean("mcpTransport", McpServerTransportType.class, () -> mcpTransport);
    context.registerBean("isInErrorState", Boolean.class, () -> catalog instanceof EmptyCatalog);
  }
}
