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
    public void testIsSupportedWithOpenAIProvider() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        when(options.aiProvider()).thenReturn("openai");
        when(options.model()).thenReturn("gpt-4o-mini");
        when(options.apiKey()).thenReturn("test-api-key");

        OpenAIModelFactory factory = new OpenAIModelFactory(options);

        // Act & Assert
        assertThat(factory.isSupported(), is(true));
    }

    @Test
    public void testIsSupportedWithNonOpenAIProvider() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        when(options.aiProvider()).thenReturn("other");
        when(options.model()).thenReturn("gpt-4o-mini");

        OpenAIModelFactory factory = new OpenAIModelFactory(options);

        // Act & Assert
        assertThat(factory.isSupported(), is(false));
    }

    @Test
    public void testIsSupportedWithUnsupportedModel() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        when(options.aiProvider()).thenReturn("openai");
        when(options.model()).thenReturn("unsupported-model");

        OpenAIModelFactory factory = new OpenAIModelFactory(options);

        // Act & Assert
        assertThat(factory.isSupported(), is(false));
    }

    @Test
    public void testHasEmbeddingModel() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        OpenAIModelFactory factory = new OpenAIModelFactory(options);

        // Act & Assert
        assertThat(factory.hasEmbeddingModel(), is(true));
    }

    @Test
    public void testNewChatModel() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        when(options.aiProvider()).thenReturn("openai");
        when(options.model()).thenReturn("gpt-4o-mini");
        when(options.apiKey()).thenReturn("test-api-key");
        when(options.timeout()).thenReturn(60);

        OpenAIModelFactory factory = new OpenAIModelFactory(options);

        // Act
        ChatModel chatModel = factory.newChatModel();

        // Assert
        assertThat(chatModel, is(notNullValue()));
    }

    @Test
    public void testNewChatMemory() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        when(options.model()).thenReturn("gpt-4o-mini");

        OpenAIModelFactory factory = new OpenAIModelFactory(options);

        // Act
        ChatMemory chatMemory = factory.newChatMemory();

        // Assert
        assertThat(chatMemory, is(notNullValue()));
    }

    @Test
    public void testNewEmbeddingModel() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        when(options.apiKey()).thenReturn("test-api-key");

        OpenAIModelFactory factory = new OpenAIModelFactory(options);

        // Act
        EmbeddingModel embeddingModel = factory.newEmbeddingModel();

        // Assert
        assertThat(embeddingModel, is(notNullValue()));
    }

    @Test
    public void testToString() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        OpenAIModelFactory factory = new OpenAIModelFactory(options);

        // Act & Assert
        assertThat(factory.toString(), is("openai - OpenAI"));
    }
}
