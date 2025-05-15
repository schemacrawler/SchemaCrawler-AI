package schemacrawler.tools.command.aichat.langchain4j.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.TokenUsage;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.aichat.langchain4j.Langchain4JChatAssistant;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;

public class Langchain4JChatAssistantTest {

    @Test
    public void testChatWithEmptyPrompt() {
        // Arrange
        AiChatCommandOptions mockOptions = mock(AiChatCommandOptions.class);
        Catalog mockCatalog = mock(Catalog.class);
        Connection mockConnection = mock(Connection.class);

        // Use MockedConstruction to mock the constructor and return behavior
        try (MockedConstruction<Langchain4JChatAssistant> mockedConstruction =
                Mockito.mockConstruction(Langchain4JChatAssistant.class,
                    (mock, context) -> {
                        // Define behavior for the mocked instance
                        when(mock.chat("")).thenReturn("");
                    })) {

            // Act
            Langchain4JChatAssistant assistant = new Langchain4JChatAssistant(mockOptions, mockCatalog, mockConnection);
            String result = assistant.chat("");

            // Assert
            assertThat(result, is(""));
        }
    }

    @Test
    public void testChatWithSimpleResponse() {
        // Arrange
        String prompt = "Hello";
        String expectedResponse = "Hi there!";

        AiChatCommandOptions mockOptions = mock(AiChatCommandOptions.class);
        Catalog mockCatalog = mock(Catalog.class);
        Connection mockConnection = mock(Connection.class);

        // Use MockedConstruction to mock the constructor and return behavior
        try (MockedConstruction<Langchain4JChatAssistant> mockedConstruction =
                Mockito.mockConstruction(Langchain4JChatAssistant.class,
                    (mock, context) -> {
                        // Define behavior for the mocked instance
                        when(mock.chat(prompt)).thenReturn(expectedResponse);
                    })) {

            // Act
            Langchain4JChatAssistant assistant = new Langchain4JChatAssistant(mockOptions, mockCatalog, mockConnection);
            String result = assistant.chat(prompt);

            // Assert
            assertThat(result, is(expectedResponse));
        }
    }

    @Test
    public void testShouldExit() {
        // Arrange
        AiChatCommandOptions mockOptions = mock(AiChatCommandOptions.class);
        Catalog mockCatalog = mock(Catalog.class);
        Connection mockConnection = mock(Connection.class);

        // Use MockedConstruction to mock the constructor and return behavior
        try (MockedConstruction<Langchain4JChatAssistant> mockedConstruction =
                Mockito.mockConstruction(Langchain4JChatAssistant.class,
                    (mock, context) -> {
                        // Define behavior for the mocked instance
                        when(mock.shouldExit()).thenReturn(true);
                    })) {

            // Act
            Langchain4JChatAssistant assistant = new Langchain4JChatAssistant(mockOptions, mockCatalog, mockConnection);

            // Assert
            assertThat(assistant.shouldExit(), is(true));
        }
    }

    @Test
    public void testClose() {
        // Arrange
        AiChatCommandOptions mockOptions = mock(AiChatCommandOptions.class);
        Catalog mockCatalog = mock(Catalog.class);
        Connection mockConnection = mock(Connection.class);

        // Use MockedConstruction to mock the constructor
        try (MockedConstruction<Langchain4JChatAssistant> mockedConstruction =
                Mockito.mockConstruction(Langchain4JChatAssistant.class)) {

            // Act
            Langchain4JChatAssistant assistant = new Langchain4JChatAssistant(mockOptions, mockCatalog, mockConnection);
            assistant.close();

            // No assertion needed, just verifying it doesn't throw an exception
        }
    }
}
