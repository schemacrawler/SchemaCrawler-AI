package schemacrawler.tools.command.aichat.utility.lanchain4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static java.util.Objects.requireNonNull;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.command.aichat.embeddings.EmbeddingService;
import schemacrawler.tools.command.aichat.embeddings.QueryService;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;
import schemacrawler.tools.command.aichat.utility.lanchain4j.AiModelFactoryUtility.AiModelFactory;
import schemacrawler.tools.command.serialize.model.TableDocument;
import us.fatehi.utility.Utility;

public class CatalogContentRetriever implements ContentRetriever {

  private final QueryService queryService;
  private final Content databaseInfoContent;

  public CatalogContentRetriever(final AiChatCommandOptions commandOptions, final Catalog catalog) {
    requireNonNull(commandOptions, "No model factory provided");
    requireNonNull(catalog, "No catalog provided");

    final AiModelFactory modelFactory = AiModelFactoryUtility.chooseAiModelFactory(commandOptions);
    if (modelFactory == null) {
      throw new SchemaCrawlerException("No models found");
    }
    databaseInfoContent = getDatabaseInfoContent(catalog);

    if (commandOptions.isUseMetadata()) {
      final EmbeddingService embeddingService = new Langchain4JEmbeddingService(modelFactory);
      queryService = new QueryService(embeddingService);
      queryService.addTables(catalog.getTables());
    } else {
      queryService = null;
    }
  }

  @Override
  public List<Content> retrieve(final Query query) {
    final List<Content> contents = new ArrayList<>();
    contents.add(databaseInfoContent);
    if (queryService == null) {
      return contents;
    }
    final Collection<TableDocument> tableDocuments = queryService.query(query.text());
    for (final TableDocument tableDocument : tableDocuments) {
      final Metadata metadata = new Metadata();
      metadata.put("table-name", tableDocument.getTableName());
      metadata.put("table-schema", Utility.trimToEmpty(tableDocument.getSchema()));
      final TextSegment textSegment = TextSegment.from(tableDocument.toJson(), metadata);
      contents.add(Content.from(textSegment));
    }
    System.err.print(String.format("Retrieving %s tables%n", contents.size() - 1));
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
