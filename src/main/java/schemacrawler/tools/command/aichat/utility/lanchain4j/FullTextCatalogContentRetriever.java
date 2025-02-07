package schemacrawler.tools.command.aichat.utility.lanchain4j;

import java.util.List;
import java.util.logging.Logger;
import org.apache.lucene.store.Directory;
import static java.util.Objects.requireNonNull;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.lucene.DirectoryFactory;
import dev.langchain4j.rag.content.retriever.lucene.LuceneContentRetriever;
import dev.langchain4j.rag.content.retriever.lucene.LuceneEmbeddingStore;
import dev.langchain4j.rag.query.Query;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.Table;
import schemacrawler.tools.command.serialize.model.CompactCatalogUtility;
import schemacrawler.tools.command.serialize.model.TableDocument;

public class FullTextCatalogContentRetriever implements ContentRetriever {

  private static final Logger LOGGER =
      Logger.getLogger(Langchain4JChatAssistant.class.getCanonicalName());

  private final LuceneContentRetriever fullTextCatalogRetriever;

  public FullTextCatalogContentRetriever(final EmbeddingModel embeddingModel, final Catalog catalog) {
    requireNonNull(catalog, "No catalog provided");

    final Directory tempDirectory = DirectoryFactory.tempDirectory();
    final LuceneEmbeddingStore luceneIndexer = LuceneEmbeddingStore.builder().directory(tempDirectory).build();
    final TextSegment databaseInfoContent = getDatabaseInfoContent(catalog);
    luceneIndexer.add(databaseInfoContent);
    for (final Table table : catalog.getTables()) {
      final TableDocument tableDocument = CompactCatalogUtility.getTableDocument(table, false);
      luceneIndexer.add(TextSegment.from(tableDocument.toJson()));
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
