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

package schemacrawler.tools.command.aichat.utility.lanchain4j;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import schemacrawler.tools.command.aichat.embeddings.EmbeddingService;
import schemacrawler.tools.command.aichat.embeddings.TextEmbedding;
import us.fatehi.utility.string.StringFormat;

public final class Langchain4JEmbeddingService implements EmbeddingService {

  private static final Logger LOGGER =
      Logger.getLogger(Langchain4JEmbeddingService.class.getCanonicalName());

  private final EmbeddingModel embeddingModel;

  public Langchain4JEmbeddingService(final EmbeddingModel embeddingModel) {
    this.embeddingModel = requireNonNull(embeddingModel, "No embedding model provided");
  }

  @Override
  public TextEmbedding embed(final String text) {
    requireNotBlank(text, "No text provided");

    try {
      final Response<Embedding> response = embeddingModel.embed(text);
      final long tokenCount = response.tokenUsage().totalTokenCount();
      final List<Double> embeddeding = new ArrayList<>();
      final float[] vector = response.content().vector();
      for (final float f : vector) {
        embeddeding.add((double) f);
      }
      return new TextEmbedding(text, tokenCount, embeddeding);
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, e, new StringFormat("Could not embed text"));
    }
    return new TextEmbedding(text);
  }
}
