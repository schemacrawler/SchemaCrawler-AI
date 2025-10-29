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
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.ai.mcpserver.utility.CatalogFactory;
import schemacrawler.tools.ai.tools.FunctionDefinitionRegistry;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

public class McpServerInitializer
    implements ApplicationContextInitializer<GenericApplicationContext> {

  private final boolean isInErrorState;
  private final Catalog catalog;
  private final DatabaseConnectionSource connectionSource;
  private final McpServerTransportType mcpTransport;

  public McpServerInitializer(
      final Catalog catalog,
      final Connection connection,
      final McpServerTransportType mcpTransport) {

    this.mcpTransport = requireNonNull(mcpTransport, "No MCP Server transport provided");
    if (mcpTransport == McpServerTransportType.unknown) {
      throw new SchemaCrawlerException("Unknown MCP Server transport type");
    }

    boolean isInErrorState = false;
    DatabaseConnectionSource connectionSource;
    try {
      connectionSource = DatabaseConnectionSources.fromConnection(connection);
    } catch (final Exception e) {
      if (mcpTransport != McpServerTransportType.stdio) {
        throw e;
      }
      isInErrorState = true;
      connectionSource = new EmptyDatabaseConnectionSource();
    }
    this.connectionSource = connectionSource;
    this.isInErrorState = isInErrorState;

    this.catalog = requireNonNull(catalog, "No catalog provided");
  }

  public McpServerInitializer(final McpServerContext context) {
    requireNonNull(context, "No context provided");

    mcpTransport = context.getMcpTransport();

    // Load the catalog with the catalog data source
    boolean isInErrorState = false;
    Catalog catalog;
    try {
      catalog = context.getCatalog();
    } catch (final Exception e) {
      if (mcpTransport != McpServerTransportType.stdio) {
        throw e;
      }
      catalog = CatalogFactory.createEmptyCatalog(e);
      isInErrorState = true;
    }
    this.catalog = catalog;
    this.isInErrorState = isInErrorState;

    // Once the catalog is loaded, use the operations database connection source
    DatabaseConnectionSource connectionSource;
    try {
      connectionSource = context.buildOperationsDatabaseConnectionSource();
    } catch (final Exception e) {
      if (mcpTransport != McpServerTransportType.stdio) {
        throw e;
      }
      connectionSource = new EmptyDatabaseConnectionSource();
    }
    this.connectionSource = connectionSource;
  }

  @Override
  public void initialize(@NonNull final GenericApplicationContext context) {
    context.registerBean("mcpTransport", McpServerTransportType.class, () -> mcpTransport);
    context.registerBean(
        "databaseConnectionSource", DatabaseConnectionSource.class, () -> connectionSource);
    context.registerAlias("databaseConnectionSource", "connectionSource");
    context.registerBean("catalog", Catalog.class, () -> catalog);
    context.registerBean("isInErrorState", Boolean.class, () -> isInErrorState);
    // Register services
    context.registerBean(
        "functionDefinitionRegistry",
        FunctionDefinitionRegistry.class,
        () -> FunctionDefinitionRegistry.getFunctionDefinitionRegistry());
  }
}
