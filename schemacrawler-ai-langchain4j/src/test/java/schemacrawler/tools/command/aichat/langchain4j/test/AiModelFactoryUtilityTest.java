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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.command.aichat.langchain4j.AiModelFactoryUtility;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;

public class AiModelFactoryUtilityTest {

  @Test
  public void testChooseAiModelFactoryWithAnthropic() {
    // Arrange
    final AiChatCommandOptions options = mock(AiChatCommandOptions.class);
    when(options.aiProvider()).thenReturn("anthropic");
    when(options.model()).thenReturn("claude-3-haiku-20240307");
    when(options.apiKey()).thenReturn("test-api-key");

    // Act
    final Object factory = AiModelFactoryUtility.chooseAiModelFactory(options);

    // Assert
    assertThat(factory, is(notNullValue()));
    assertThat(factory.toString(), is("anthropic - Anthropic"));
  }

  @Test
  @Disabled("GitHub model factory test is disabled because we can't easily mock the static method")
  public void testChooseAiModelFactoryWithGitHub() {
    // Arrange
    final AiChatCommandOptions options = mock(AiChatCommandOptions.class);
    when(options.aiProvider()).thenReturn("github-models");
    when(options.model()).thenReturn("llama-3-8b-instruct");
    when(options.apiKey()).thenReturn("test-api-key");

    // Act
    final Object factory = AiModelFactoryUtility.chooseAiModelFactory(options);

    // Assert
    assertThat(factory, is(notNullValue()));
    assertThat(factory.toString(), is("github-models - GitHub Models"));
  }

  @Test
  public void testChooseAiModelFactoryWithNullOptions() {
    // Act & Assert
    try {
      AiModelFactoryUtility.chooseAiModelFactory(null);
      // If we get here, the test should fail
      assertThat("Expected NullPointerException was not thrown", false);
    } catch (final NullPointerException e) {
      assertThat(e.getMessage(), is("No AI Chat options provided"));
    }
  }

  @Test
  public void testChooseAiModelFactoryWithOpenAI() {
    // Arrange
    final AiChatCommandOptions options = mock(AiChatCommandOptions.class);
    when(options.aiProvider()).thenReturn("openai");
    when(options.model()).thenReturn("gpt-4o-mini");
    when(options.apiKey()).thenReturn("test-api-key");

    // Act
    final Object factory = AiModelFactoryUtility.chooseAiModelFactory(options);

    // Assert
    assertThat(factory, is(notNullValue()));
    assertThat(factory.toString(), is("openai - OpenAI"));
  }

  @Test
  public void testChooseAiModelFactoryWithUnsupportedProvider() {
    // Arrange
    final AiChatCommandOptions options = mock(AiChatCommandOptions.class);
    when(options.aiProvider()).thenReturn("unsupported");

    // Act
    final Object factory = AiModelFactoryUtility.chooseAiModelFactory(options);

    // Assert
    assertThat(factory, is(nullValue()));
  }
}
