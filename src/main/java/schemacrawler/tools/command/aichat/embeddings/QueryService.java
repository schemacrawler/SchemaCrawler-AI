/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
 */

package schemacrawler.tools.command.aichat.embeddings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import io.github.sashirestela.openai.SimpleOpenAI;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.string.ObjectToStringFormat;
import us.fatehi.utility.string.StringFormat;

public final class QueryService {

  private static final Logger LOGGER = Logger.getLogger(QueryService.class.getCanonicalName());

  private static final int MAX_TOKENS = 8_000; // Do not make this too small

  private final String metadataPriming;
  private final TableEmbeddingService tableEmbeddingService;
  private final TableSimilarityService tableSimilarityService;

  public QueryService(final SimpleOpenAI service) {
    requireNonNull(service, "No Open AI service provided");
    final EmbeddingService embeddingService = new EmbeddingService(service);
    tableEmbeddingService = new TableEmbeddingService(embeddingService);
    tableSimilarityService = new TableSimilarityService(embeddingService);
    metadataPriming = IOUtility.readResourceFully("/metadata-priming.txt");
    if (isBlank(metadataPriming)) {
      throw new ConfigurationException("Could not load metadata priming text");
    }
  }

  public void addTables(final Collection<Table> tables) {
    requireNonNull(tables, "No tables provided");

    LOGGER.log(Level.INFO, "Embedding all tables in the catalog");
    for (final Table table : tables) {
      final EmbeddedTable embeddingTable = tableEmbeddingService.embedTable(table);
      tableSimilarityService.addTable(embeddingTable);
    }
  }

  /**
   * Get a list of system messages that can provide background information for the AI agent.
   *
   * @param prompt User prompt
   * @return System prompts and table metadata
   */
  public Collection<String> query(final String prompt) {
    LOGGER.log(Level.INFO, new StringFormat("Searching for tables matching prompt:%n%s", prompt));

    final Collection<String> messages = new ArrayList<>();

    final Collection<EmbeddedTable> matchedTables =
        tableSimilarityService.query(prompt, MAX_TOKENS);
    LOGGER.log(Level.CONFIG, new ObjectToStringFormat("Tables matching prompt", matchedTables));
    if (matchedTables.isEmpty()) {
      return messages;
    }

    messages.add(metadataPriming);
    for (final EmbeddedTable embeddedTable : matchedTables) {
      messages.add(embeddedTable.toJson());
    }
    return messages;
  }
}
