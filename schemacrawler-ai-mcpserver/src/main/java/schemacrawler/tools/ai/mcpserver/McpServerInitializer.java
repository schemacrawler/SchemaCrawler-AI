/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.MapPropertySource;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.importance.model.ImportanceModel;
import schemacrawler.importance.model.implementation.ImportanceModelBuilder;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.ai.mcpserver.server.DatabaseIdentity;
import schemacrawler.tools.ai.mcpserver.utility.DatabaseConnectionSourceUtility;
import schemacrawler.tools.ai.mcpserver.utility.DatabaseIdentityUtility;
import schemacrawler.tools.ai.mcpserver.utility.InErrorFactory;
import schemacrawler.tools.ai.tools.FunctionDefinitionRegistry;
import schemacrawler.tools.state.AbstractExecutionState;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public class McpServerInitializer extends AbstractExecutionState
    implements ApplicationContextInitializer<GenericApplicationContext> {

  private static final Logger LOGGER = Logger.getLogger(McpServerInitializer.class.getName());

  private static final String INSTRUCTIONS_PROPERTY = "spring.ai.mcp.server.instructions";
  private static final String TOOL_USAGE_GUIDE_RESOURCE = "tool-usage-guide.md";

  static String toolUsageGuide() {
    try (final InputStream in =
        McpServerInitializer.class.getResourceAsStream(TOOL_USAGE_GUIDE_RESOURCE)) {
      requireNonNull(in, "Tool usage guide not found");
      // Normalize line endings, since the resource may be checked out with CRLF
      return new String(in.readAllBytes(), StandardCharsets.UTF_8).replace("\r\n", "\n").strip();
    } catch (final IOException e) {
      throw new UncheckedIOException("Could not read tool usage guide", e);
    }
  }

  private final boolean isInErrorState;
  private final McpServerTransportType mcpTransport;
  private final ExcludeTools excludeTools;
  private final DatabaseIdentity databaseIdentity;

  public McpServerInitializer(
      final Catalog catalog,
      final DatabaseConnectionSource connectionSource,
      final McpServerTransportType mcpTransport,
      final Collection<String> excludeTools) {
    this(catalog, connectionSource, mcpTransport, excludeTools, null, null);
  }

  public McpServerInitializer(
      final Catalog catalog,
      final DatabaseConnectionSource connectionSource,
      final McpServerTransportType mcpTransport,
      final Collection<String> excludeTools,
      final String databaseAlias,
      final String databaseDescription) {

    this.mcpTransport = requireNonNull(mcpTransport, "No MCP Server transport provided");
    if (mcpTransport == McpServerTransportType.unknown) {
      throw new ExecutionRuntimeException("Unknown MCP Server transport type");
    }

    boolean isInErrorState = !DatabaseConnectionSourceUtility.canConnect(connectionSource);

    if (catalog == null) {
      isInErrorState = true;
    }

    if (isInErrorState) {
      setCatalog(InErrorFactory.createErroredCatalog());
      setERModel(InErrorFactory.createErroredERModel());
      setConnectionSource(InErrorFactory.createErroredConnectionSource());
    } else {
      setCatalog(catalog);
      setERModel(SchemaCrawlerUtility.buildERModel(catalog));
      setConnectionSource(connectionSource);
    }
    this.isInErrorState = isInErrorState;

    databaseIdentity =
        DatabaseIdentityUtility.from(databaseAlias, databaseDescription, nonErroredCatalog());

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
      LOGGER.log(Level.WARNING, "Could not load catalog", e);
      if (mcpTransport != McpServerTransportType.stdio) {
        throw e;
      }
      catalog = InErrorFactory.createErroredCatalog();
      isInErrorState = true;
    }
    setCatalog(catalog);

    if (!isInErrorState) {
      setERModel(SchemaCrawlerUtility.buildERModel(catalog));
    } else {
      setERModel(InErrorFactory.createErroredERModel());
    }

    // Once the catalog is loaded, use the operations database connection source
    if (!isInErrorState) {
      DatabaseConnectionSource connectionSource;
      try {
        connectionSource = scContext.buildOperationsDatabaseConnectionSource();
        requireNonNull(connectionSource, "Connection source is not built");
      } catch (final Exception e) {
        if (mcpTransport != McpServerTransportType.stdio) {
          throw e;
        }
        connectionSource = InErrorFactory.createErroredConnectionSource();
      }
      setConnectionSource(connectionSource);
    } else {
      setConnectionSource(InErrorFactory.createErroredConnectionSource());
    }

    this.isInErrorState = isInErrorState;

    databaseIdentity =
        DatabaseIdentityUtility.from(
            scContext.databaseAlias(), scContext.databaseDescription(), nonErroredCatalog());

    excludeTools = new ExcludeTools(context.excludeTools());
  }

  @Override
  public void initialize(@NonNull final GenericApplicationContext context) {

    final Catalog catalog = getCatalog();
    final ERModel erModel = getERModel();
    final DatabaseConnectionSource connectionSource = getConnectionSource();
    final ImportanceModel importanceModel =
        isInErrorState ? null : ImportanceModelBuilder.builder(catalog).build();

    context.registerBean("mcpTransport", McpServerTransportType.class, () -> mcpTransport);
    context.registerBean(
        "databaseConnectionSource", DatabaseConnectionSource.class, () -> connectionSource);
    context.registerAlias("databaseConnectionSource", "connectionSource");
    context.registerBean("catalog", Catalog.class, () -> catalog);
    context.registerBean("erModel", ERModel.class, () -> erModel);
    if (importanceModel != null) {
      context.registerBean("importanceModel", ImportanceModel.class, () -> importanceModel);
    }
    context.registerBean("isInErrorState", Boolean.class, () -> isInErrorState);
    context.registerBean(
        "isOffline",
        Boolean.class,
        () -> DatabaseConnectionSourceUtility.isOffline(connectionSource));
    // Register services
    context.registerBean(
        "functionDefinitionRegistry",
        FunctionDefinitionRegistry.class,
        () -> FunctionDefinitionRegistry.getFunctionDefinitionRegistry());
    context.registerBean("excludeTools", ExcludeTools.class, () -> excludeTools);
    context.registerBean("databaseIdentity", DatabaseIdentity.class, () -> databaseIdentity);

    // Highest precedence, so that the generated instructions cannot be overridden
    context
        .getEnvironment()
        .getPropertySources()
        .addFirst(
            new MapPropertySource(
                "schemacrawlerInstructions", Map.of(INSTRUCTIONS_PROPERTY, instructions())));
  }

  private String instructions() {
    final String description = databaseIdentity.description();
    final String toolUsageGuide = toolUsageGuide();
    if (isBlank(description)) {
      return toolUsageGuide;
    }
    return description + "\n\n" + toolUsageGuide;
  }

  // The errored catalog throws on every call, so it cannot provide identity values
  private Catalog nonErroredCatalog() {
    return isInErrorState ? null : getCatalog();
  }
}
