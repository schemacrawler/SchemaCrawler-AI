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
import dev.langchain4j.model.embedding.EmbeddingModel;
import schemacrawler.tools.command.aichat.langchain4j.AnthropicModelFactory;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;

public class AnthropicModelFactoryTest {

    @Test
    public void testIsSupportedWithAnthropicProvider() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        when(options.aiProvider()).thenReturn("anthropic");
        when(options.model()).thenReturn("claude-3-haiku-20240307");
        when(options.apiKey()).thenReturn("test-api-key");

        AnthropicModelFactory factory = new AnthropicModelFactory(options);

        // Act & Assert
        assertThat(factory.isSupported(), is(true));
    }

    @Test
    public void testIsSupportedWithNonAnthropicProvider() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        when(options.aiProvider()).thenReturn("other");

        AnthropicModelFactory factory = new AnthropicModelFactory(options);

        // Act & Assert
        assertThat(factory.isSupported(), is(false));
    }

    @Test
    public void testIsSupportedWithUnsupportedModel() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        when(options.aiProvider()).thenReturn("anthropic");
        when(options.model()).thenReturn("unsupported-model");

        AnthropicModelFactory factory = new AnthropicModelFactory(options);

        // Act & Assert
        assertThat(factory.isSupported(), is(false));
    }

    @Test
    public void testHasEmbeddingModel() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        AnthropicModelFactory factory = new AnthropicModelFactory(options);

        // Act & Assert
        assertThat(factory.hasEmbeddingModel(), is(false));
    }

    @Test
    public void testNewChatModel() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        when(options.aiProvider()).thenReturn("anthropic");
        when(options.model()).thenReturn("claude-3-haiku-20240307");
        when(options.apiKey()).thenReturn("test-api-key");
        when(options.timeout()).thenReturn(60);

        AnthropicModelFactory factory = new AnthropicModelFactory(options);

        // Act
        ChatModel chatModel = factory.newChatModel();

        // Assert
        assertThat(chatModel, is(notNullValue()));
    }

    @Test
    public void testNewChatMemory() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        when(options.context()).thenReturn(10);

        AnthropicModelFactory factory = new AnthropicModelFactory(options);

        // Act
        ChatMemory chatMemory = factory.newChatMemory();

        // Assert
        assertThat(chatMemory, is(notNullValue()));
    }

    @Test
    public void testNewEmbeddingModel() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        AnthropicModelFactory factory = new AnthropicModelFactory(options);

        // Act & Assert
        // Anthropic doesn't support embedding models in this implementation
        UnsupportedFeatureException exception = assertThrows(
            UnsupportedFeatureException.class,
            () -> factory.newEmbeddingModel()
        );

        assertThat(exception.getMessage(), is("Anthropic does not have embedding models"));
    }

    @Test
    public void testToString() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        AnthropicModelFactory factory = new AnthropicModelFactory(options);

        // Act & Assert
        assertThat(factory.toString(), is("anthropic - Anthropic"));
    }
}
