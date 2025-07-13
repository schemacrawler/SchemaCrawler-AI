/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

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
import schemacrawler.schema.TableType;
import schemacrawler.tools.command.aichat.langchain4j.FullTextCatalogContentRetriever;

public class FullTextCatalogContentRetrieverTest {

  @Test
  public void testRetrieveWithEmbeddingModel() {
    // Arrange
    final EmbeddingModel embeddingModel = mock(EmbeddingModel.class);
    final Catalog catalog = createMockCatalog();

    final FullTextCatalogContentRetriever retriever =
        new FullTextCatalogContentRetriever(embeddingModel, catalog);
    final Query query = Query.from("test query");

    // Act
    final List<Content> contents = retriever.retrieve(query);

    // Assert
    assertThat(contents, is(notNullValue()));
    assertThat(contents, is(not(empty())));

    // Verify content contains table information
    boolean foundTableContent = false;
    for (final Content content : contents) {
      final String text = content.textSegment().text();
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
    final DatabaseInfo databaseInfo = mock(DatabaseInfo.class);
    when(databaseInfo.getDatabaseProductName()).thenReturn("Test Database");
    when(databaseInfo.getDatabaseProductVersion()).thenReturn("1.0");

    final Catalog catalog = mock(Catalog.class);
    when(catalog.getSchemas()).thenReturn(Arrays.asList());
    when(catalog.getDatabaseInfo()).thenReturn(databaseInfo);

    final FullTextCatalogContentRetriever retriever =
        new FullTextCatalogContentRetriever(null, catalog);
    final Query query = Query.from("test query");

    // Act
    final List<Content> contents = retriever.retrieve(query);

    // Assert
    assertThat(contents, is(notNullValue()));
    assertThat(contents, hasSize(1)); // Database info is always added even with empty catalog
  }

  @Test
  public void testRetrieveWithoutEmbeddingModel() {
    // Arrange
    final Catalog catalog = createMockCatalog();

    final FullTextCatalogContentRetriever retriever =
        new FullTextCatalogContentRetriever(null, catalog);
    final Query query = Query.from("test query");

    // Act
    final List<Content> contents = retriever.retrieve(query);

    // Assert
    assertThat(contents, is(notNullValue()));
    assertThat(contents, is(not(empty())));

    // Verify content contains table information
    boolean foundTableContent = false;
    for (final Content content : contents) {
      final String text = content.textSegment().text();
      if (text.contains("TEST_TABLE")) {
        foundTableContent = true;
        break;
      }
    }

    assertThat("Content should include table information", foundTableContent, is(true));
  }

  private Catalog createMockCatalog() {
    // Create mock schema
    final Schema schema = mock(Schema.class);
    when(schema.getName()).thenReturn("PUBLIC");
    when(schema.getFullName()).thenReturn("PUBLIC");

    // Create mock tables
    final Table table1 = mock(Table.class);
    when(table1.getName()).thenReturn("TEST_TABLE");
    when(table1.getFullName()).thenReturn("PUBLIC.TEST_TABLE");
    when(table1.getRemarks()).thenReturn("Test table remarks");
    when(table1.getTableType()).thenReturn(TableType.UNKNOWN);
    when(table1.getSchema()).thenReturn(schema);

    // Create mock catalog with tables
    final Collection<Table> tables = Arrays.asList(table1);

    // Create mock database info
    final DatabaseInfo databaseInfo = mock(DatabaseInfo.class);
    when(databaseInfo.getDatabaseProductName()).thenReturn("Test Database");
    when(databaseInfo.getDatabaseProductVersion()).thenReturn("1.0");

    // Create mock catalog
    final Catalog catalog = mock(Catalog.class);
    when(catalog.getSchemas()).thenReturn(Arrays.asList(schema));
    when(catalog.getTables()).thenReturn(tables);
    when(catalog.getDatabaseInfo()).thenReturn(databaseInfo);

    return catalog;
  }
}
