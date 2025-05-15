package schemacrawler.tools.command.aichat.langchain4j.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.query.Query;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.tools.command.aichat.langchain4j.FullTextCatalogContentRetriever;

public class FullTextCatalogContentRetrieverTest {

    @Test
    public void testRetrieveWithEmbeddingModel() {
        // Arrange
        EmbeddingModel embeddingModel = mock(EmbeddingModel.class);
        Catalog catalog = createMockCatalog();

        FullTextCatalogContentRetriever retriever = new FullTextCatalogContentRetriever(embeddingModel, catalog);
        Query query = Query.from("test query");

        // Act
        List<Content> contents = retriever.retrieve(query);

        // Assert
        assertThat(contents, is(notNullValue()));
        assertThat(contents, is(not(empty())));

        // Verify content contains table information
        boolean foundTableContent = false;
        for (Content content : contents) {
            String text = content.textSegment().text();
            if (text.contains("TEST_TABLE")) {
                foundTableContent = true;
                break;
            }
        }

        assertThat("Content should include table information", foundTableContent, is(true));
    }

    @Test
    public void testRetrieveWithoutEmbeddingModel() {
        // Arrange
        Catalog catalog = createMockCatalog();

        FullTextCatalogContentRetriever retriever = new FullTextCatalogContentRetriever(null, catalog);
        Query query = Query.from("test query");

        // Act
        List<Content> contents = retriever.retrieve(query);

        // Assert
        assertThat(contents, is(notNullValue()));
        assertThat(contents, is(not(empty())));

        // Verify content contains table information
        boolean foundTableContent = false;
        for (Content content : contents) {
            String text = content.textSegment().text();
            if (text.contains("TEST_TABLE")) {
                foundTableContent = true;
                break;
            }
        }

        assertThat("Content should include table information", foundTableContent, is(true));
    }

    @Test
    public void testRetrieveWithEmptyCatalog() {
        // Arrange
        // Create mock database info
        DatabaseInfo databaseInfo = mock(DatabaseInfo.class);
        when(databaseInfo.getDatabaseProductName()).thenReturn("Test Database");
        when(databaseInfo.getDatabaseProductVersion()).thenReturn("1.0");

        Catalog catalog = mock(Catalog.class);
        when(catalog.getSchemas()).thenReturn(Arrays.asList());
        when(catalog.getDatabaseInfo()).thenReturn(databaseInfo);

        FullTextCatalogContentRetriever retriever = new FullTextCatalogContentRetriever(null, catalog);
        Query query = Query.from("test query");

        // Act
        List<Content> contents = retriever.retrieve(query);

        // Assert
        assertThat(contents, is(notNullValue()));
        assertThat(contents, hasSize(1)); // Database info is always added even with empty catalog
    }

    private Catalog createMockCatalog() {
        // Create mock schema
        Schema schema = mock(Schema.class);
        when(schema.getName()).thenReturn("PUBLIC");
        when(schema.getFullName()).thenReturn("PUBLIC");

        // Create mock tables
        Table table1 = mock(Table.class);
        when(table1.getName()).thenReturn("TEST_TABLE");
        when(table1.getFullName()).thenReturn("PUBLIC.TEST_TABLE");
        when(table1.getRemarks()).thenReturn("Test table remarks");
        when(table1.getSchema()).thenReturn(schema);

        // Create mock catalog with tables
        Collection<Table> tables = Arrays.asList(table1);

        // Create mock database info
        DatabaseInfo databaseInfo = mock(DatabaseInfo.class);
        when(databaseInfo.getDatabaseProductName()).thenReturn("Test Database");
        when(databaseInfo.getDatabaseProductVersion()).thenReturn("1.0");

        // Create mock catalog
        Catalog catalog = mock(Catalog.class);
        when(catalog.getSchemas()).thenReturn(Arrays.asList(schema));
        when(catalog.getTables()).thenReturn(tables);
        when(catalog.getDatabaseInfo()).thenReturn(databaseInfo);

        return catalog;
    }
}
