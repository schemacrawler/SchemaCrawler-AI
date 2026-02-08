package schemacrawler.tools.ai.mcpserver.server;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.test.utility.crawl.LightCatalogUtility;
import schemacrawler.test.utility.crawl.LightRoutine;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.tools.ai.mcpserver.ExcludeTools;
import schemacrawler.tools.ai.mcpserver.McpServerTransportType;
import us.fatehi.test.utility.TestObjectUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ResourceProviderTest.MockConfig.class, ResourceProvider.class})
public class ResourceProviderTest {

  @TestConfiguration
  static class MockConfig {
    @Bean
    Catalog catalog() {
      return spy(LightCatalogUtility.lightCatalog());
    }

    @Bean
    DatabaseConnectionSource databaseConnectionSource() {
      return LightCatalogUtility.lightDatabaseConnectionSource();
    }

    @Bean
    ERModel erModel() {
      return TestObjectUtility.makeTestObject(ERModel.class);
    }

    @Bean
    ExcludeTools excludeTools() {
      return new ExcludeTools();
    }

    @Bean
    boolean isInErrorState() {
      return false;
    }

    @Bean
    McpServerTransportType mcpTransport() {
      return McpServerTransportType.http;
    }

    @Bean
    ServerHealth serverHealth() {
      final ServerHealth serverHealth = mock(ServerHealth.class);
      return serverHealth;
    }
  }

  private static SchemaReference schema = new SchemaReference("PUBLIC", "BOOKS");

  @Autowired private ResourceProvider resourceProvider;

  @Autowired private Catalog catalog;

  private final Table table1 = new LightTable(schema, "BOOKS");
  private final Table table2 = new LightTable(new SchemaReference("PUBLIC", "OTHER"), "BOOKS");

  @Test
  public void testAmbiguousMatch() {
    when(catalog.getTables()).thenReturn(List.of(table1, table2));

    final IORuntimeException e =
        assertThrows(IORuntimeException.class, () -> resourceProvider.getTableDetails("BOOKS"));
    assertThat(e.getMessage(), is("<BOOKS> has too many matches - provide a fully-qualified name"));
  }

  @Test
  public void testGetRoutineDetails() {
    final Routine routine = new LightRoutine(schema, "NEW_BOOK");
    when(catalog.getRoutines()).thenReturn(List.of(routine));

    final String details = resourceProvider.getRoutineDetails("PUBLIC.BOOKS.NEW_BOOK");
    assertThat(details, is(containsString("NEW_BOOK")));
  }

  @Test
  public void testGetRoutineDetailsNotFound() {
    when(catalog.getRoutines()).thenReturn(Collections.emptyList());
    final IORuntimeException e =
        assertThrows(
            IORuntimeException.class, () -> resourceProvider.getRoutineDetails("NON_EXISTENT"));
    assertThat(e.getMessage(), is("<NON_EXISTENT> not found"));
  }

  @Test
  public void testGetTableDetails() {
    when(catalog.getTables()).thenReturn(List.of(table1));

    final String details = resourceProvider.getTableDetails("PUBLIC.BOOKS.BOOKS");
    assertThat(details, is(containsString("BOOKS")));
  }

  @Test
  public void testGetTableDetailsNotFound() {
    when(catalog.getTables()).thenReturn(Collections.emptyList());
    final IORuntimeException e =
        assertThrows(
            IORuntimeException.class, () -> resourceProvider.getTableDetails("NON_EXISTENT"));
    assertThat(e.getMessage(), is("<NON_EXISTENT> not found"));
  }
}
