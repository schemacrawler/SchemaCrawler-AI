/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver;

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.util.Collection;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.ermodel.utility.EntityModelUtility;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.ai.mcpserver.utility.EmptyFactory;
import schemacrawler.tools.ai.tools.FunctionDefinitionRegistry;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

public class McpServerInitializer
    implements ApplicationContextInitializer<GenericApplicationContext> {

  private final boolean isInErrorState;
  private final Catalog catalog;
  private final ERModel erModel;
  private final DatabaseConnectionSource connectionSource;
  private final McpServerTransportType mcpTransport;
  private final ExcludeTools excludeTools;

  public McpServerInitializer(
      final Catalog catalog,
      final Connection connection,
      final McpServerTransportType mcpTransport,
      final Collection<String> excludeTools) {

    this.mcpTransport = requireNonNull(mcpTransport, "No MCP Server transport provided");
    if (mcpTransport == McpServerTransportType.unknown) {
      throw new ExecutionRuntimeException("Unknown MCP Server transport type");
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
      connectionSource = EmptyFactory.createEmptyDatabaseConnectionSource();
    }
    this.connectionSource = connectionSource;
    this.isInErrorState = isInErrorState;

    this.catalog = requireNonNull(catalog, "No catalog provided");
    if (!isInErrorState) {
      erModel = EntityModelUtility.buildERModel(catalog);
    } else {
      erModel = EmptyFactory.createEmptyERModel();
    }

    if (excludeTools == null) {
      this.excludeTools = new ExcludeTools();
    } else {
      this.excludeTools = new ExcludeTools(excludeTools);
    }
  }

  public McpServerInitializer(
      final SchemaCrawlerContext scContext, final McpServerContext context) {
    requireNonNull(scContext, "No SchemaCrawler context provided");
    requireNonNull(context, "No MCP Server context provided");

    mcpTransport = context.mcpTransport();

    // Load the catalog with the catalog data source
    boolean isInErrorState = false;
    Catalog catalog;
    try {
      catalog = scContext.loadCatalog();
    } catch (final Exception e) {
      if (mcpTransport != McpServerTransportType.stdio) {
        throw e;
      }
      catalog = EmptyFactory.createEmptyCatalog(e);
      isInErrorState = true;
    }
    this.catalog = catalog;
    if (!isInErrorState) {
      erModel = EntityModelUtility.buildERModel(catalog);
    } else {
      erModel = EmptyFactory.createEmptyERModel();
    }
    this.isInErrorState = isInErrorState;

    // Once the catalog is loaded, use the operations database connection source
    DatabaseConnectionSource connectionSource;
    try {
      connectionSource = scContext.buildOperationsDatabaseConnectionSource();
    } catch (final Exception e) {
      if (mcpTransport != McpServerTransportType.stdio) {
        throw e;
      }
      connectionSource = EmptyFactory.createEmptyDatabaseConnectionSource();
    }
    this.connectionSource = connectionSource;

    excludeTools = new ExcludeTools(context.excludeTools());
  }

  @Override
  public void initialize(@NonNull final GenericApplicationContext context) {
    context.registerBean("mcpTransport", McpServerTransportType.class, () -> mcpTransport);
    context.registerBean(
        "databaseConnectionSource", DatabaseConnectionSource.class, () -> connectionSource);
    context.registerAlias("databaseConnectionSource", "connectionSource");
    context.registerBean("catalog", Catalog.class, () -> catalog);
    context.registerBean("erModel", ERModel.class, () -> erModel);
    context.registerBean("isInErrorState", Boolean.class, () -> isInErrorState);
    // Register services
    context.registerBean(
        "functionDefinitionRegistry",
        FunctionDefinitionRegistry.class,
        () -> FunctionDefinitionRegistry.getFunctionDefinitionRegistry());
    context.registerBean("excludeTools", ExcludeTools.class, () -> excludeTools);
  }
}
