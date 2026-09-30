/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.mcpserver.server;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import org.junit.jupiter.api.Test;

public class PromptProviderTest {

  private final PromptProvider promptProvider = new PromptProvider();

  @Test
  public void shouldProvideDatabaseExpertPrompt() {
    final GetPromptResult result = promptProvider.databaseExpert();

    assertThat(result.description(), is("Database expert prompt"));
    assertThat(result.messages().size(), is(1));
    final PromptMessage promptMessage = result.messages().getFirst();
    assertThat(promptMessage.role(), is(Role.USER));
    final String text = ((TextContent) promptMessage.content()).text();
    assertThat(text.contains("relational database expert"), is(true));
    assertThat(text.contains("Only include facts retrieved"), is(true));
    assertThat(text.endsWith("\n\n"), is(true));
  }

  @Test
  public void shouldProvideSqlQueryAssistantPrompt() {
    final GetPromptResult result = promptProvider.sqlQueryAssistant();

    assertThat(result.description(), is("SQL query assistant prompt"));
    assertThat(result.messages().size(), is(1));
    final PromptMessage promptMessage = result.messages().getFirst();
    assertThat(promptMessage.role(), is(Role.USER));
    final String text = ((TextContent) promptMessage.content()).text();
    assertThat(text.contains("expert SQL developer"), is(true));
    assertThat(text.contains("schema-qualified names"), is(true));
    assertThat(text.endsWith("\n\n"), is(true));
  }
}
