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

package schemacrawler.tools.command.chatgpt.test.utility;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import io.github.sashirestela.openai.OpenAI.Embeddings;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.common.Usage;
import io.github.sashirestela.openai.domain.embedding.Embedding;
import io.github.sashirestela.openai.domain.embedding.EmbeddingFloat;
import io.github.sashirestela.openai.domain.embedding.EmbeddingRequest;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class AITestUtility {

  private static class EmbeddingResult extends Embedding<EmbeddingFloat> {

    private final EmbeddingFloat embeddingFloat;
    private final Usage usage;

    public EmbeddingResult(final EmbeddingFloat embeddingFloat, final Usage usage) {
      this.embeddingFloat = embeddingFloat;
      this.usage = usage;
    }

    @Override
    public List<EmbeddingFloat> getData() {
      return Collections.singletonList(embeddingFloat);
    }

    @Override
    public Usage getUsage() {
      return usage;
    }
  }

  public static SimpleOpenAI mockAiService(final List<Double> expectedEmbedding) {
    try {
      final SimpleOpenAI service = mock(SimpleOpenAI.class);

      final Usage usage = mock(Usage.class);
      when(usage.getPromptTokens()).thenReturn(101);

      final EmbeddingFloat embeddingFloat = mock(EmbeddingFloat.class);
      when(embeddingFloat.getEmbedding())
          .thenReturn(new ArrayList<>(Collections.singletonList(0.5)));

      final EmbeddingResult embeddingResult = new EmbeddingResult(embeddingFloat, usage);

      final CompletableFuture<Embedding<EmbeddingFloat>> futureEmbeddingResult =
          CompletableFuture.completedFuture(embeddingResult);

      final Embeddings embeddings = mock(Embeddings.class);

      when(service.embeddings()).thenReturn(embeddings);
      when(embeddings.create(any(EmbeddingRequest.class))).thenReturn(futureEmbeddingResult);

      return service;
    } catch (final Exception e) {
      fail(e);
      return null;
    }
  }

  private AITestUtility() {
    // Prevent instantiation
  }
}
