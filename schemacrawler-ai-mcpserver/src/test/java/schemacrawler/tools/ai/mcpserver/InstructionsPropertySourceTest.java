/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.ai.mcpserver.server.DatabaseIdentity;
import schemacrawler.tools.ai.mcpserver.utility.VocabularyTerms;
import schemacrawler.vocabulary.Vocabulary;
import schemacrawler.vocabulary.VocabularyTerm;

public class InstructionsPropertySourceTest {

  private static final String INSTRUCTIONS_PROPERTY = "spring.ai.mcp.server.instructions";
  private static final String TERMS_HEADER = "# Terms Used in This Database";

  private static Vocabulary vocabulary(final int termCount, final boolean trusted) {
    final List<VocabularyTerm> terms =
        IntStream.range(0, termCount)
            .mapToObj(
                i -> new VocabularyTerm("word%02d".formatted(i), "word%02d".formatted(i), 100 - i))
            .toList();
    return new Vocabulary(terms, trusted);
  }

  private static String instructions(final boolean inError, final Vocabulary vocabulary) {
    return (String)
        new InstructionsPropertySource(inError, DatabaseIdentity.empty(), vocabulary)
            .getProperty(INSTRUCTIONS_PROPERTY);
  }

  @Test
  public void errorState() {
    assertThat(instructions(true, vocabulary(12, true)), not(containsString(TERMS_HEADER)));
  }

  @Test
  public void trustedVocabulary() {
    final String without = instructions(false, null);
    final String with = instructions(false, vocabulary(60, true));

    assertThat(with, containsString(TERMS_HEADER));
    assertThat(with, containsString(VocabularyTerms.USAGE_SENTENCE));
    assertThat(with, containsString("word00 (100), word01 (99)"));
    assertThat(with, containsString("word39 (61)"));
    // Capped
    assertThat(with, not(containsString("word40")));
    // Rest of the instructions are unchanged
    assertThat(with.startsWith(without), is(true));
  }

  @Test
  public void untrustedVocabulary() {
    assertThat(instructions(false, vocabulary(12, false)), is(instructions(false, null)));
  }
}
