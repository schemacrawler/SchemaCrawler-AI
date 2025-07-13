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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import dev.langchain4j.exception.UnsupportedFeatureException;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import schemacrawler.tools.ai.chat.ChatOptions;
import schemacrawler.tools.command.aichat.langchain4j.AnthropicModelFactory;

public class AnthropicModelFactoryTest {

  @Test
  public void testHasEmbeddingModel() {
    // Arrange
    final ChatOptions options = mock(ChatOptions.class);
    final AnthropicModelFactory factory = new AnthropicModelFactory(options);

    // Act & Assert
    assertThat(factory.hasEmbeddingModel(), is(false));
  }

  @Test
  public void testIsSupportedWithAnthropicProvider() {
    // Arrange
    final ChatOptions options = mock(ChatOptions.class);
    when(options.aiProvider()).thenReturn("anthropic");
    when(options.model()).thenReturn("claude-3-haiku-20240307");
    when(options.apiKey()).thenReturn("test-api-key");

    final AnthropicModelFactory factory = new AnthropicModelFactory(options);

    // Act & Assert
    assertThat(factory.isSupported(), is(true));
  }

  @Test
  public void testIsSupportedWithNonAnthropicProvider() {
    // Arrange
    final ChatOptions options = mock(ChatOptions.class);
    when(options.aiProvider()).thenReturn("other");

    final AnthropicModelFactory factory = new AnthropicModelFactory(options);

    // Act & Assert
    assertThat(factory.isSupported(), is(false));
  }

  @Test
  public void testIsSupportedWithUnsupportedModel() {
    // Arrange
    final ChatOptions options = mock(ChatOptions.class);
    when(options.aiProvider()).thenReturn("anthropic");
    when(options.model()).thenReturn("unsupported-model");

    final AnthropicModelFactory factory = new AnthropicModelFactory(options);

    // Act & Assert
    assertThat(factory.isSupported(), is(false));
  }

  @Test
  public void testNewChatMemory() {
    // Arrange
    final ChatOptions options = mock(ChatOptions.class);
    when(options.context()).thenReturn(10);

    final AnthropicModelFactory factory = new AnthropicModelFactory(options);

    // Act
    final ChatMemory chatMemory = factory.newChatMemory();

    // Assert
    assertThat(chatMemory, is(notNullValue()));
  }

  @Test
  public void testNewChatModel() {
    // Arrange
    final ChatOptions options = mock(ChatOptions.class);
    when(options.aiProvider()).thenReturn("anthropic");
    when(options.model()).thenReturn("claude-3-haiku-20240307");
    when(options.apiKey()).thenReturn("test-api-key");
    when(options.timeout()).thenReturn(60);

    final AnthropicModelFactory factory = new AnthropicModelFactory(options);

    // Act
    final ChatModel chatModel = factory.newChatModel();

    // Assert
    assertThat(chatModel, is(notNullValue()));
  }

  @Test
  public void testNewEmbeddingModel() {
    // Arrange
    final ChatOptions options = mock(ChatOptions.class);
    final AnthropicModelFactory factory = new AnthropicModelFactory(options);

    // Act & Assert
    // Anthropic doesn't support embedding models in this implementation
    final UnsupportedFeatureException exception =
        assertThrows(UnsupportedFeatureException.class, () -> factory.newEmbeddingModel());

    assertThat(exception.getMessage(), is("Anthropic does not have embedding models"));
  }

  @Test
  public void testToString() {
    // Arrange
    final ChatOptions options = mock(ChatOptions.class);
    final AnthropicModelFactory factory = new AnthropicModelFactory(options);

    // Act & Assert
    assertThat(factory.toString(), is("anthropic - Anthropic"));
  }
}
