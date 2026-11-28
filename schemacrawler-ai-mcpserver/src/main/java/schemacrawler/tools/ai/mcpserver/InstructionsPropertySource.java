/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static us.fatehi.utility.Utility.isBlank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import org.springframework.core.env.MapPropertySource;
import schemacrawler.tools.ai.mcpserver.server.DatabaseIdentity;
import schemacrawler.tools.ai.mcpserver.utility.VocabularyTerms;
import schemacrawler.vocabulary.Vocabulary;
import schemacrawler.vocabulary.VocabularyTerm;
import us.fatehi.utility.ioresource.ClasspathInputResource;

public class InstructionsPropertySource extends MapPropertySource {

  private static final String INSTRUCTIONS_PROPERTY = "spring.ai.mcp.server.instructions";

  private static String createDatabaseServerDescription(final DatabaseIdentity databaseIdentity) {
    if (databaseIdentity == null || databaseIdentity.isEmpty()) {
      return "";
    }

    final StringBuilder description = new StringBuilder();
    description.append(
        """
        # Database Server Description

        """);
    if (!isBlank(databaseIdentity.alias())) {
      description.append(
          """
          The SchemaCrawler AI MCP Server is connected to a database server that has
          an alias of "%s", to distinguish itself from other instances of
          SchemaCrawler AI MCP Server that may be connected to other database servers.

          """
              .formatted(databaseIdentity.alias()));
    }
    if (!isBlank(databaseIdentity.description())) {
      description.append(
          """
          The description of this database server is:
          %s

          """
              .formatted(databaseIdentity.description()));
    }
    description.append(
        """

        Use the `about_database` tool to learn more about the database engine, version
        and settings.

        """);

    return description.toString();
  }

  private static String createTermsSection(final Vocabulary vocabulary) {
    final List<VocabularyTerm> terms = VocabularyTerms.displayTerms(vocabulary);
    if (terms.isEmpty()) {
      return "";
    }
    final String termList =
        terms.stream()
            .map(term -> "%s (%d)".formatted(term.word(), term.objectCount()))
            .collect(joining(", "));
    return """

    # Terms Used in This Database

    %s

    %s
    """
        .formatted(VocabularyTerms.USAGE_SENTENCE, termList);
  }

  private static String instructions(
      final boolean isInErrorState,
      final DatabaseIdentity databaseIdentity,
      final Vocabulary vocabulary) {
    final String instructions;
    final String toolUsageGuide = toolUsageGuide();
    if (isInErrorState) {
      instructions =
          """
          The SchemaCrawler AI MCP Server could not connect to the database.
          MCP tool calls will not return reliable results.
          However, note that tool listings and other MCP server features
          are available.
          """;
    } else {
      final String databaseServerDescription = createDatabaseServerDescription(databaseIdentity);
      final String vocabularySection = createTermsSection(vocabulary);
      instructions = databaseServerDescription + toolUsageGuide + vocabularySection;
    }
    return instructions;
  }

  private static String toolUsageGuide() {
    try (final BufferedReader reader =
        new ClasspathInputResource("tool-usage-guide.md").openNewInputReader(UTF_8)) {
      return reader.lines().collect(joining("\n"));
    } catch (final IOException e) {
      throw new UncheckedIOException("Could not read tool usage guide", e);
    }
  }

  public InstructionsPropertySource(
      final boolean isInErrorState,
      final DatabaseIdentity databaseIdentity,
      final Vocabulary vocabulary) {
    super(
        "mcpServerInstructions",
        Map.of(INSTRUCTIONS_PROPERTY, instructions(isInErrorState, databaseIdentity, vocabulary)));
  }
}
