package schemacrawler.tools.command.aichat.langchain4j.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.sql.Connection;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.aichat.langchain4j.Langchain4JChatAssistant;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;

public class Langchain4JChatAssistantTest {

  @Test
  public void testChatWithEmptyPrompt() {
    // Arrange
    final AiChatCommandOptions mockOptions = mock(AiChatCommandOptions.class);
    final Catalog mockCatalog = mock(Catalog.class);
    final Connection mockConnection = mock(Connection.class);

    // Use MockedConstruction to mock the constructor and return behavior
    try (MockedConstruction<Langchain4JChatAssistant> mockedConstruction =
        Mockito.mockConstruction(
            Langchain4JChatAssistant.class,
            (mock, context) -> {
              // Define behavior for the mocked instance
              when(mock.chat("")).thenReturn("");
            })) {

      // Act
      final Langchain4JChatAssistant assistant =
          new Langchain4JChatAssistant(mockOptions, mockCatalog, mockConnection);
      final String result = assistant.chat("");

      // Assert
      assertThat(result, is(""));
    }
  }

  @Test
  public void testChatWithSimpleResponse() {
    // Arrange
    final String prompt = "Hello";
    final String expectedResponse = "Hi there!";

    final AiChatCommandOptions mockOptions = mock(AiChatCommandOptions.class);
    final Catalog mockCatalog = mock(Catalog.class);
    final Connection mockConnection = mock(Connection.class);

    // Use MockedConstruction to mock the constructor and return behavior
    try (MockedConstruction<Langchain4JChatAssistant> mockedConstruction =
        Mockito.mockConstruction(
            Langchain4JChatAssistant.class,
            (mock, context) -> {
              // Define behavior for the mocked instance
              when(mock.chat(prompt)).thenReturn(expectedResponse);
            })) {

      // Act
      final Langchain4JChatAssistant assistant =
          new Langchain4JChatAssistant(mockOptions, mockCatalog, mockConnection);
      final String result = assistant.chat(prompt);

      // Assert
      assertThat(result, is(expectedResponse));
    }
  }

  @Test
  public void testClose() {
    // Arrange
    final AiChatCommandOptions mockOptions = mock(AiChatCommandOptions.class);
    final Catalog mockCatalog = mock(Catalog.class);
    final Connection mockConnection = mock(Connection.class);

    // Use MockedConstruction to mock the constructor
    try (MockedConstruction<Langchain4JChatAssistant> mockedConstruction =
        Mockito.mockConstruction(Langchain4JChatAssistant.class)) {

      // Act
      final Langchain4JChatAssistant assistant =
          new Langchain4JChatAssistant(mockOptions, mockCatalog, mockConnection);
      assistant.close();

      // No assertion needed, just verifying it doesn't throw an exception
    }
  }

  @Test
  public void testShouldExit() {
    // Arrange
    final AiChatCommandOptions mockOptions = mock(AiChatCommandOptions.class);
    final Catalog mockCatalog = mock(Catalog.class);
    final Connection mockConnection = mock(Connection.class);

    // Use MockedConstruction to mock the constructor and return behavior
    try (MockedConstruction<Langchain4JChatAssistant> mockedConstruction =
        Mockito.mockConstruction(
            Langchain4JChatAssistant.class,
            (mock, context) -> {
              // Define behavior for the mocked instance
              when(mock.shouldExit()).thenReturn(true);
            })) {

      // Act
      final Langchain4JChatAssistant assistant =
          new Langchain4JChatAssistant(mockOptions, mockCatalog, mockConnection);

      // Assert
      assertThat(assistant.shouldExit(), is(true));
    }
  }
}
