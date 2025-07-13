/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.langchain4j.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import schemacrawler.tools.command.aichat.langchain4j.OpenAIModelFactory;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;

public class OpenAIModelFactoryTest {

  @Test
  public void testHasEmbeddingModel() {
    // Arrange
    final AiChatCommandOptions options = mock(AiChatCommandOptions.class);
    final OpenAIModelFactory factory = new OpenAIModelFactory(options);

    // Act & Assert
    assertThat(factory.hasEmbeddingModel(), is(true));
  }

  @Test
  public void testIsSupportedWithNonOpenAIProvider() {
    // Arrange
    final AiChatCommandOptions options = mock(AiChatCommandOptions.class);
    when(options.aiProvider()).thenReturn("other");
    when(options.model()).thenReturn("gpt-4o-mini");

    final OpenAIModelFactory factory = new OpenAIModelFactory(options);

    // Act & Assert
    assertThat(factory.isSupported(), is(false));
  }

  @Test
  public void testIsSupportedWithOpenAIProvider() {
    // Arrange
    final AiChatCommandOptions options = mock(AiChatCommandOptions.class);
    when(options.aiProvider()).thenReturn("openai");
    when(options.model()).thenReturn("gpt-4o-mini");
    when(options.apiKey()).thenReturn("test-api-key");

    final OpenAIModelFactory factory = new OpenAIModelFactory(options);

    // Act & Assert
    assertThat(factory.isSupported(), is(true));
  }

  @Test
  public void testIsSupportedWithUnsupportedModel() {
    // Arrange
    final AiChatCommandOptions options = mock(AiChatCommandOptions.class);
    when(options.aiProvider()).thenReturn("openai");
    when(options.model()).thenReturn("unsupported-model");

    final OpenAIModelFactory factory = new OpenAIModelFactory(options);

    // Act & Assert
    assertThat(factory.isSupported(), is(false));
  }

  @Test
  public void testNewChatMemory() {
    // Arrange
    final AiChatCommandOptions options = mock(AiChatCommandOptions.class);
    when(options.model()).thenReturn("gpt-4o-mini");

    final OpenAIModelFactory factory = new OpenAIModelFactory(options);

    // Act
    final ChatMemory chatMemory = factory.newChatMemory();

    // Assert
    assertThat(chatMemory, is(notNullValue()));
  }

  @Test
  public void testNewChatModel() {
    // Arrange
    final AiChatCommandOptions options = mock(AiChatCommandOptions.class);
    when(options.aiProvider()).thenReturn("openai");
    when(options.model()).thenReturn("gpt-4o-mini");
    when(options.apiKey()).thenReturn("test-api-key");
    when(options.timeout()).thenReturn(60);

    final OpenAIModelFactory factory = new OpenAIModelFactory(options);

    // Act
    final ChatModel chatModel = factory.newChatModel();

    // Assert
    assertThat(chatModel, is(notNullValue()));
  }

  @Test
  public void testNewEmbeddingModel() {
    // Arrange
    final AiChatCommandOptions options = mock(AiChatCommandOptions.class);
    when(options.apiKey()).thenReturn("test-api-key");

    final OpenAIModelFactory factory = new OpenAIModelFactory(options);

    // Act
    final EmbeddingModel embeddingModel = factory.newEmbeddingModel();

    // Assert
    assertThat(embeddingModel, is(notNullValue()));
  }

  @Test
  public void testToString() {
    // Arrange
    final AiChatCommandOptions options = mock(AiChatCommandOptions.class);
    final OpenAIModelFactory factory = new OpenAIModelFactory(options);

    // Act & Assert
    assertThat(factory.toString(), is("openai - OpenAI"));
  }
}
