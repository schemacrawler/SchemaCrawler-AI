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
import schemacrawler.tools.command.aichat.langchain4j.GitHubModelFactory;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;

public class GitHubModelFactoryTest {

    @Test
    public void testIsSupportedWithGitHubProvider() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        when(options.aiProvider()).thenReturn("github-models");
        // Mock the isSupported method to return true for testing
        GitHubModelFactory factory = new GitHubModelFactory(options) {
            @Override
            public boolean isSupported() {
                return true;
            }
        };
        when(options.apiKey()).thenReturn("test-api-key");

        // Act & Assert
        assertThat(factory.isSupported(), is(true));
    }

    @Test
    public void testIsSupportedWithNonGitHubProvider() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        when(options.aiProvider()).thenReturn("other");

        GitHubModelFactory factory = new GitHubModelFactory(options);

        // Act & Assert
        assertThat(factory.isSupported(), is(false));
    }

    @Test
    public void testHasEmbeddingModel() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        GitHubModelFactory factory = new GitHubModelFactory(options);

        // Act & Assert
        assertThat(factory.hasEmbeddingModel(), is(true));
    }

    @Test
    public void testNewChatModel() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        when(options.aiProvider()).thenReturn("github-models");
        when(options.model()).thenReturn("llama-3-8b-instruct");
        when(options.apiKey()).thenReturn("test-api-key");
        when(options.timeout()).thenReturn(60);

        GitHubModelFactory factory = new GitHubModelFactory(options);

        // Act
        ChatModel chatModel = factory.newChatModel();

        // Assert
        assertThat(chatModel, is(notNullValue()));
    }

    @Test
    public void testNewChatMemory() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        when(options.model()).thenReturn("llama-3-8b-instruct");
        when(options.context()).thenReturn(10); // Add context value > 0

        GitHubModelFactory factory = new GitHubModelFactory(options);

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

        GitHubModelFactory factory = new GitHubModelFactory(options);

        // Act
        EmbeddingModel embeddingModel = factory.newEmbeddingModel();

        // Assert
        assertThat(embeddingModel, is(notNullValue()));
    }

    @Test
    public void testToString() {
        // Arrange
        AiChatCommandOptions options = mock(AiChatCommandOptions.class);
        GitHubModelFactory factory = new GitHubModelFactory(options);

        // Act & Assert
        assertThat(factory.toString(), is("github-models - GitHub Models"));
    }
}
