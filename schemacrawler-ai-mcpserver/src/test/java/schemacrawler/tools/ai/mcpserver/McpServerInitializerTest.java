package schemacrawler.tools.ai.mcpserver;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public class McpServerInitializerTest {

  @Test
  public void testMcpServerInitializerConstructor1() {
    final Catalog catalog = mock(Catalog.class);
    final Connection connection = mock(Connection.class);
    final McpServerInitializer initializer =
        new McpServerInitializer(
            catalog, connection, McpServerTransportType.stdio, Collections.emptyList());

    final GenericApplicationContext context = new GenericApplicationContext();
    initializer.initialize(context);
    context.refresh();

    assertThat(context.getBean("catalog"), is(catalog));
    assertThat(context.getBean("mcpTransport"), is(McpServerTransportType.stdio));
    assertThat(
        context.getBean("databaseConnectionSource"), instanceOf(DatabaseConnectionSource.class));
  }

  @Test
  public void testMcpServerInitializerConstructor2() {
    final SchemaCrawlerContext scContext = mock(SchemaCrawlerContext.class);
    final McpServerContext context = mock(McpServerContext.class);
    final Catalog catalog = mock(Catalog.class);

    when(scContext.loadCatalog()).thenReturn(catalog);
    when(context.mcpTransport()).thenReturn(McpServerTransportType.stdio);
    when(context.excludeTools()).thenReturn(Collections.emptyList());

    final McpServerInitializer initializer = new McpServerInitializer(scContext, context);

    final GenericApplicationContext springContext = new GenericApplicationContext();
    initializer.initialize(springContext);
    springContext.refresh();

    assertThat(springContext.getBean("catalog"), is(catalog));
    assertThat(springContext.getBean("mcpTransport"), is(McpServerTransportType.stdio));
  }

  @Test
  public void testUnknownTransport() {
    final Catalog catalog = mock(Catalog.class);
    final Connection connection = mock(Connection.class);
    assertThrows(
        ExecutionRuntimeException.class,
        () ->
            new McpServerInitializer(
                catalog, connection, McpServerTransportType.unknown, Collections.emptyList()));
  }

  @Test
  public void testErrorInNonStdioTransport() {
    final Catalog catalog = mock(Catalog.class);
    final Connection connection =
        null; // This will cause error in DatabaseConnectionSources.fromConnection

    assertThrows(
        NullPointerException.class,
        () ->
            new McpServerInitializer(
                catalog, connection, McpServerTransportType.http, Collections.emptyList()));
  }

  @Test
  public void testErrorInStdioTransport() {
    final Catalog catalog = mock(Catalog.class);
    final Connection connection = null;

    // In stdio, it should not throw but set isInErrorState to true
    final McpServerInitializer initializer =
        new McpServerInitializer(
            catalog, connection, McpServerTransportType.stdio, Collections.emptyList());

    final GenericApplicationContext context = new GenericApplicationContext();
    initializer.initialize(context);
    context.refresh();

    assertThat(context.getBean("isInErrorState", Boolean.class), is(true));
  }
}
