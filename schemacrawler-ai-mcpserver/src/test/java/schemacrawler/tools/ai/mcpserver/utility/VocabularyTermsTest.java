/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.utility;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.vocabulary.Vocabulary;
import schemacrawler.vocabulary.VocabularyTerm;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

public class VocabularyTermsTest {

  public static Vocabulary vocabulary(final int termCount, final boolean trusted) {
    final List<VocabularyTerm> terms = new ArrayList<>();
    for (int i = 0; i < termCount; i++) {
      final String word = "word%02d".formatted(i);
      terms.add(new VocabularyTerm(word, word, 100 - i));
    }
    return new Vocabulary(terms, trusted);
  }

  @Test
  public void absentVocabulary() {
    final ObjectNode node = mapper.createObjectNode();
    assertThat(VocabularyTerms.addTerms(node, null), is(false));
    assertThat(node.size(), is(0));
    assertThat(VocabularyTerms.displayTerms(null), hasSize(0));
  }

  @Test
  public void cappedTerms() {
    final ObjectNode node = mapper.createObjectNode();
    assertThat(VocabularyTerms.addTerms(node, vocabulary(60, true)), is(true));
    final JsonNode terms = node.get("terms");
    assertThat(terms.size(), is(VocabularyTerms.DISPLAY_TERM_LIMIT));
    assertThat(terms.get(0).get("word").asString(), is("word00"));
    assertThat(terms.get(0).get("objects").asInt(), is(100));
  }

  @Test
  public void trustedTerms() {
    final ObjectNode node = mapper.createObjectNode();
    assertThat(VocabularyTerms.addTerms(node, vocabulary(12, true)), is(true));
    assertThat(node.get("terms").size(), is(12));
    assertThat(node.get("terms_usage").asString(), is(VocabularyTerms.USAGE_SENTENCE));
  }

  @Test
  public void untrustedVocabulary() {
    final ObjectNode node = mapper.createObjectNode();
    assertThat(VocabularyTerms.addTerms(node, vocabulary(12, false)), is(false));
    assertThat(node.size(), is(0));
    assertThat(node.get("terms"), is(nullValue()));
  }
}
