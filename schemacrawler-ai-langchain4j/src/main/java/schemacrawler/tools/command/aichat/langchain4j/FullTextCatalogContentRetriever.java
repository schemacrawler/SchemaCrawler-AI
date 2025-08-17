/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.langchain4j;

import static java.util.Objects.requireNonNull;

import dev.langchain4j.community.rag.content.retriever.lucene.LuceneContentRetriever;
import dev.langchain4j.community.rag.content.retriever.lucene.LuceneEmbeddingStore;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.lucene.store.Directory;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.Table;
import schemacrawler.tools.ai.model.AdditionalTableDetails;
import schemacrawler.tools.ai.model.CatalogDocument;
import schemacrawler.tools.ai.model.CompactCatalogUtility;
import schemacrawler.tools.ai.model.TableDocument;

public class FullTextCatalogContentRetriever implements ContentRetriever {

  private static final Logger LOGGER =
      Logger.getLogger(Langchain4JChatAssistant.class.getCanonicalName());

  private final LuceneContentRetriever fullTextCatalogRetriever;

  public FullTextCatalogContentRetriever(
      final EmbeddingModel embeddingModel, final Catalog catalog) {
    requireNonNull(catalog, "No catalog provided");

    final Directory tempDirectory = DirectoryFactory.tempDirectory();
    final Map<AdditionalTableDetails, Boolean> allTableDetails = CatalogDocument.allTableDetails();
    final LuceneEmbeddingStore luceneIndexer =
        LuceneEmbeddingStore.builder().directory(tempDirectory).build();
    final TextSegment databaseInfoContent = getDatabaseInfoContent(catalog);
    luceneIndexer.add(databaseInfoContent);
    for (final Table table : catalog.getTables()) {
      final TableDocument tableDocument =
          new CompactCatalogUtility()
              .withAdditionalTableDetails(allTableDetails)
              .getTableDocument(table);
      luceneIndexer.add(TextSegment.from(tableDocument.toObjectNode().toPrettyString()));
    }

    fullTextCatalogRetriever =
        LuceneContentRetriever.builder()
            .directory(tempDirectory)
            .embeddingModel(embeddingModel)
            .matchUntilMaxResults()
            .maxTokens(5_000)
            .build();
  }

  @Override
  public List<Content> retrieve(final Query query) {
    return fullTextCatalogRetriever.retrieve(query);
  }

  private TextSegment getDatabaseInfoContent(final Catalog catalog) {
    final DatabaseInfo databaseInfo = catalog.getDatabaseInfo();
    final String databaseProductName = databaseInfo.getDatabaseProductName();
    final Metadata metadata = new Metadata();
    metadata.put("database", databaseProductName);
    metadata.put("database-version", databaseInfo.getDatabaseProductVersion());
    final TextSegment textSegment =
        TextSegment.from(
            String.format("Customize SQL queries for %s", databaseProductName), metadata);
    return textSegment;
  }
}
