package schemacrawler.tools.command.aichat.utility.lanchain4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static java.util.Objects.requireNonNull;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.tools.command.aichat.embeddings.EmbeddingService;
import schemacrawler.tools.command.aichat.embeddings.QueryService;

public class CatalogContentRetriever implements ContentRetriever {

  private final QueryService queryService;
  private final Content databaseInfoContent;

  public CatalogContentRetriever(final EmbeddingModel embeddingModel, final Catalog catalog) {
    requireNonNull(embeddingModel, "No embedding model provided");
    requireNonNull(catalog, "No catalog provided");

    databaseInfoContent = getDatabaseInfoContent(catalog);

    final EmbeddingService embeddingService = new Langchain4JEmbeddingService(embeddingModel);
    queryService = new QueryService(embeddingService);
    queryService.addTables(catalog.getTables());
  }

  @Override
  public List<Content> retrieve(final Query query) {
    final List<Content> contents = new ArrayList<>();
    contents.add(databaseInfoContent);
    final Collection<String> tableDocuments = queryService.query(query.text());
    System.err.print(String.format("Retrieving %s tables%n", tableDocuments.size()));
    for (final String tableDocument : tableDocuments) {
      contents.add(Content.from(tableDocument));
    }
    return contents;
  }

  private Content getDatabaseInfoContent(final Catalog catalog) {
    final DatabaseInfo databaseInfo = catalog.getDatabaseInfo();
    final String databaseProductName = databaseInfo.getDatabaseProductName();
    final Metadata metadata = new Metadata();
    metadata.put("database", databaseProductName);
    metadata.put("database-version", databaseInfo.getDatabaseProductVersion());
    final TextSegment textSegment =
        TextSegment.from(
            String.format("Customize SQL queries for %s", databaseProductName), metadata);
    return Content.from(textSegment);
  }
}
