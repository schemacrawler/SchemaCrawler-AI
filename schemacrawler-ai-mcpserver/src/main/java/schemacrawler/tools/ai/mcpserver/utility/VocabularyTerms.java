/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.utility;

import java.util.List;
import schemacrawler.vocabulary.Vocabulary;
import schemacrawler.vocabulary.VocabularyTerm;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import us.fatehi.utility.UtilityMarker;

/**
 * Builds the schema vocabulary terms that the MCP server instructions and the <code>about_database
 * </code> result show, so that both show the same thing. Nothing is produced when the vocabulary is
 * absent or not trusted.
 */
@UtilityMarker
public final class VocabularyTerms {

  /** Maximum number of terms shown. */
  public static final int DISPLAY_TERM_LIMIT = 40;

  /** One sentence on how to use the terms. */
  public static final String USAGE_SENTENCE =
      "These are the most used terms in object names, with the number of objects using each. "
          + "Use them in name patterns for `list` and `list_members_of_tables`.";

  /**
   * Adds the <code>terms</code> array and the <code>terms_usage</code> sentence to a result object,
   * if the vocabulary is trusted and has terms.
   *
   * @param resultNode Result object to add to
   * @param vocabulary Vocabulary, may be null
   * @return True if the terms were added
   */
  public static boolean addTerms(final ObjectNode resultNode, final Vocabulary vocabulary) {
    final List<VocabularyTerm> terms = displayTerms(vocabulary);
    if (resultNode == null || terms.isEmpty()) {
      return false;
    }
    final ArrayNode termsNode = resultNode.putArray("terms");
    for (final VocabularyTerm term : terms) {
      termsNode.addObject().put("word", term.word()).put("objects", term.objectCount());
    }
    resultNode.put("terms_usage", USAGE_SENTENCE);
    return true;
  }

  /**
   * The terms to show, which is empty if the vocabulary is absent or not trusted.
   *
   * @param vocabulary Vocabulary, may be null
   * @return Capped list of terms
   */
  public static List<VocabularyTerm> displayTerms(final Vocabulary vocabulary) {
    if (vocabulary == null || !vocabulary.isTrusted()) {
      return List.of();
    }
    return vocabulary.topTerms(DISPLAY_TERM_LIMIT);
  }

  private VocabularyTerms() {
    // Utility class
  }
}
