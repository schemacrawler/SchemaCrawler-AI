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

package schemacrawler.tools.command.aichat.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.sashirestela.openai.SimpleOpenAI;
import schemacrawler.tools.command.aichat.embeddings.EmbeddingService;
import schemacrawler.tools.command.aichat.test.utility.AITestUtility;
import schemacrawler.tools.command.simpleopenai.utility.SimpleOpenAIEmbeddingService;

public class EmbeddingServiceTest {

  private SimpleOpenAI service;
  private EmbeddingService embeddingService;
  private List<Double> expectedEmbedding;

  @BeforeEach
  void setUp() {
    expectedEmbedding = Collections.singletonList(0.5);
    service = AITestUtility.mockAiService(expectedEmbedding);
    embeddingService = new SimpleOpenAIEmbeddingService(service);
  }

  @Test
  void testEmbeddingWithEmptyText() {
    final String text = "";

    final IllegalArgumentException exception =
        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class, () -> embeddingService.embed(text));

    assertThat(exception.getMessage(), is("No text provided"));
  }

  @Test
  void testEmbeddingWithValidText() {
    final String text = "example text";

    final double l1Norm = embeddingService.embed(text).getEmbeddingVector().getL1Norm();

    assertThat(l1Norm, is(expectedEmbedding.get(0)));
  }
}
