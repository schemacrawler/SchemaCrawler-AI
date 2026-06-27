package schemacrawler.tools.ai.mcpserver;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public class McpServerInitializerTest {

  private Catalog catalog;

  @BeforeEach
  public void setupCatalog() {
    catalog = mock(Catalog.class);
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
    assertThat(context.getBean("mcpTransport"), is(McpServerTransportType.stdio));
    assertThat(context.getBean("isInErrorState", Boolean.class), is(false));
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
