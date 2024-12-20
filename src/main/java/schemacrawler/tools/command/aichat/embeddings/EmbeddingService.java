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

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.embedding.Embedding;
import io.github.sashirestela.openai.domain.embedding.EmbeddingFloat;
import io.github.sashirestela.openai.domain.embedding.EmbeddingRequest;
import us.fatehi.utility.string.StringFormat;

public final class EmbeddingService {

  private static final Logger LOGGER = Logger.getLogger(EmbeddingService.class.getCanonicalName());

  private static final String TEXT_EMBEDDING_MODEL = "text-embedding-3-small";

  private final SimpleOpenAI service;

  public EmbeddingService(final SimpleOpenAI service) {
    this.service = requireNonNull(service, "No Open AI service provided");
  }

  public TextEmbedding embed(final String text) {
    requireNotBlank(text, "No text provided");

    try {
      final EmbeddingRequest embeddingRequest =
          EmbeddingRequest.builder()
              .model(TEXT_EMBEDDING_MODEL)
              .input(Collections.singletonList(text))
              .build();
      final Embedding<EmbeddingFloat> embeddingResult =
          service.embeddings().create(embeddingRequest).get();
      return new TextEmbedding(text, embeddingResult);
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, e, new StringFormat("Could not embed text"));
    }
    return new TextEmbedding(text);
  }
}
