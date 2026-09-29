package schemacrawler.tools.ai.mcpserver;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.MapPropertySource;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.test.utility.crawl.LightCatalogUtility;
import schemacrawler.tools.ai.mcpserver.server.DatabaseIdentity;
import schemacrawler.tools.ai.mcpserver.utility.DatabaseIdentityUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public class McpServerInitializerTest {

  private static final String INSTRUCTIONS_PROPERTY = "spring.ai.mcp.server.instructions";

  private Catalog catalog;

  @BeforeEach
  public void setupCatalog() {
    catalog = LightCatalogUtility.lightCatalog();
  }

  @Test
  public void testDatabaseIdentityFromCatalogConstructor() {
    final DatabaseConnectionSource connectionSource = mock(DatabaseConnectionSource.class);
    final McpServerInitializer initializer =
        new McpServerInitializer(
            catalog, connectionSource, McpServerTransportType.stdio, Collections.emptyList());

    final DatabaseIdentity identity =
        getContext(initializer).getBean("databaseIdentity", DatabaseIdentity.class);

    assertThat(identity.alias(), is(""));
    assertThat(identity.description(), is(""));
    // A mock connection source cannot connect, so the server is in an error state
    assertThat(identity.serverFingerprint().fingerprint(), is(""));
    assertThat(identity.databaseProduct().getName(), is(""));
  }

  @Test
  public void testDatabaseIdentityFromCatalogConstructorWithoutAlias() {
    final DatabaseConnectionSource connectionSource = mock(DatabaseConnectionSource.class);
    final McpServerInitializer initializer =
        new McpServerInitializer(
            catalog, connectionSource, McpServerTransportType.stdio, Collections.emptyList());

    final DatabaseIdentity identity =
        getContext(initializer).getBean("databaseIdentity", DatabaseIdentity.class);

    assertThat(identity.alias(), is(""));
    assertThat(identity.description(), is(""));
  }

  @Test
  public void testDatabaseIdentityFromEnvironmentConstructor() {
    final SchemaCrawlerContext scContext = mock(SchemaCrawlerContext.class);
    final McpServerContext serverContext = mock(McpServerContext.class);

    when(scContext.loadCatalog()).thenReturn(catalog);
    when(scContext.databaseAlias()).thenReturn("crm-prod");
    when(scContext.databaseDescription()).thenReturn("CRM system of record");
    when(serverContext.mcpTransport()).thenReturn(McpServerTransportType.stdio);
    when(serverContext.excludeTools()).thenReturn(Collections.emptyList());

    final McpServerInitializer initializer = new McpServerInitializer(scContext, serverContext);

    final DatabaseIdentity identity =
        getContext(initializer).getBean("databaseIdentity", DatabaseIdentity.class);

    final DatabaseIdentity expected =
        DatabaseIdentityUtility.from("crm-prod", "CRM system of record", catalog);
    assertThat(identity, is(expected));
  }

  @Test
  public void testErrorInNonStdioTransport() {
    final DatabaseConnectionSource connectionSource = null;

    final McpServerInitializer initializer =
        new McpServerInitializer(
            catalog, connectionSource, McpServerTransportType.http, Collections.emptyList());

    final ApplicationContext context = getContext(initializer);

    assertThat(context.getBean("isInErrorState", Boolean.class), is(true));
  }

  @Test
  public void testErrorInStdioTransport() {
    final DatabaseConnectionSource connectionSource = null;

    // In stdio, it should not throw but set isInErrorState to true
    final McpServerInitializer initializer =
        new McpServerInitializer(
            catalog, connectionSource, McpServerTransportType.stdio, Collections.emptyList());

    final ApplicationContext context = getContext(initializer);

    assertThat(context.getBean("isInErrorState", Boolean.class), is(true));
  }

  @Test
  public void testMcpServerInitializerConstructor1() {
    final DatabaseConnectionSource connectionSource = mock(DatabaseConnectionSource.class);
    final McpServerInitializer initializer =
        new McpServerInitializer(
            catalog, connectionSource, McpServerTransportType.stdio, Collections.emptyList());

    final ApplicationContext context = getContext(initializer);

    assertThat(context.getBean("catalog"), instanceOf(Catalog.class));
    assertThat(context.getBean("mcpTransport"), is(McpServerTransportType.stdio));
    assertThat(
        context.getBean("databaseConnectionSource"), instanceOf(DatabaseConnectionSource.class));
    assertThat(context.getBean("isInErrorState", Boolean.class), is(true));
  }

  @Test
  public void testMcpServerInitializerConstructor2() {
    final SchemaCrawlerContext scContext = mock(SchemaCrawlerContext.class);
    final McpServerContext serverContext = mock(McpServerContext.class);

    when(scContext.loadCatalog()).thenReturn(catalog);
    when(serverContext.mcpTransport()).thenReturn(McpServerTransportType.stdio);
    when(serverContext.excludeTools()).thenReturn(Collections.emptyList());

    final McpServerInitializer initializer = new McpServerInitializer(scContext, serverContext);

    final ApplicationContext context = getContext(initializer);

    assertThat(context.getBean("catalog"), is(catalog));
    assertThat(
        context.getBean("importanceModel"),
        instanceOf(schemacrawler.importance.model.ImportanceModel.class));
    assertThat(context.getBean("mcpTransport"), is(McpServerTransportType.stdio));
    assertThat(context.getBean("isInErrorState", Boolean.class), is(false));
  }

  @Test
  public void testInstructionsCannotBeOverridden() {
    final DatabaseConnectionSource connectionSource = mock(DatabaseConnectionSource.class);
    final McpServerInitializer initializer =
        new McpServerInitializer(
            catalog, connectionSource, McpServerTransportType.stdio, Collections.emptyList());

    final GenericApplicationContext context = new GenericApplicationContext();
    context
        .getEnvironment()
        .getPropertySources()
        .addFirst(
            new MapPropertySource(
                "override", Map.of(INSTRUCTIONS_PROPERTY, "Overridden instructions")));
    initializer.initialize(context);
    context.refresh();

    assertThat(
        context.getEnvironment().getProperty(INSTRUCTIONS_PROPERTY),
        is(McpServerInitializer.toolUsageGuide()));
  }

  @Test
  public void testInstructionsInErrorStateWithDescription() {
    final DatabaseConnectionSource connectionSource = mock(DatabaseConnectionSource.class);
    final McpServerInitializer initializer =
        new McpServerInitializer(
            catalog, connectionSource, McpServerTransportType.stdio, Collections.emptyList());

    final ApplicationContext context = getContext(initializer);

    assertThat(context.getBean("isInErrorState", Boolean.class), is(true));
    assertThat(
        context.getEnvironment().getProperty(INSTRUCTIONS_PROPERTY),
        is(McpServerInitializer.toolUsageGuide()));
  }

  @Test
  public void testInstructionsWithDescription() {
    final SchemaCrawlerContext scContext = mock(SchemaCrawlerContext.class);
    final McpServerContext serverContext = mock(McpServerContext.class);

    when(scContext.loadCatalog()).thenReturn(catalog);
    when(scContext.databaseDescription()).thenReturn("CRM system of record");
    when(serverContext.mcpTransport()).thenReturn(McpServerTransportType.stdio);
    when(serverContext.excludeTools()).thenReturn(Collections.emptyList());

    final ApplicationContext context =
        getContext(new McpServerInitializer(scContext, serverContext));

    assertThat(context.getBean("isInErrorState", Boolean.class), is(false));
    final String instructions = context.getEnvironment().getProperty(INSTRUCTIONS_PROPERTY);
    assertThat(instructions, startsWith("CRM system of record\n\n"));
    assertThat(instructions, endsWith(McpServerInitializer.toolUsageGuide()));
  }

  @Test
  public void testInstructionsWithoutDescription() {
    final DatabaseConnectionSource connectionSource = mock(DatabaseConnectionSource.class);
    final McpServerInitializer initializer =
        new McpServerInitializer(
            catalog, connectionSource, McpServerTransportType.stdio, Collections.emptyList());

    final ApplicationContext context = getContext(initializer);

    assertThat(
        context.getEnvironment().getProperty(INSTRUCTIONS_PROPERTY),
        is(McpServerInitializer.toolUsageGuide()));
  }

  @Test
  public void testToolUsageGuide() {
    final String toolUsageGuide = McpServerInitializer.toolUsageGuide();

    assertThat(toolUsageGuide.isBlank(), is(false));
    assertThat(toolUsageGuide, startsWith("# "));
    assertThat(toolUsageGuide, containsString("Start here"));
    assertThat(toolUsageGuide, containsString("Use `list` for"));
    assertThat(toolUsageGuide, containsString("Use `list_members_of_tables`"));
    assertThat(
        toolUsageGuide, containsString("Use regular expression filters to keep results small."));
    // Count words only, not Markdown markers such as "#" or "1."
    final long wordCount =
        Arrays.stream(toolUsageGuide.split("\\s+"))
            .filter(word -> word.matches(".*\\p{L}.*"))
            .count();
    assertThat(wordCount, is(lessThanOrEqualTo(220L)));
  }

  @Test
  public void testUnknownTransport() {
    final DatabaseConnectionSource connectionSource = mock(DatabaseConnectionSource.class);
    assertThrows(
        ExecutionRuntimeException.class,
        () ->
            new McpServerInitializer(
                catalog,
                connectionSource,
                McpServerTransportType.unknown,
                Collections.emptyList()));
  }

  private ApplicationContext getContext(final McpServerInitializer initializer) {
    final GenericApplicationContext context = new GenericApplicationContext();
    initializer.initialize(context);
    context.refresh();
    return context;
  }
}
