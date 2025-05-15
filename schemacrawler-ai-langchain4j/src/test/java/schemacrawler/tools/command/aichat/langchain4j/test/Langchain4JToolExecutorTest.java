package schemacrawler.tools.command.aichat.langchain4j.test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.sql.Connection;

import org.junit.jupiter.api.Test;

import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.aichat.langchain4j.Langchain4JToolExecutor;

public class Langchain4JToolExecutorTest {

    @Test
    public void testExecuteWithNullRequest() {
        // Arrange
        String functionName = "testFunction";

        Catalog catalog = mock(Catalog.class);
        Connection connection = mock(Connection.class);

        // Create the tool executor
        Langchain4JToolExecutor toolExecutor = new Langchain4JToolExecutor(functionName, catalog, connection);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> toolExecutor.execute(null, null));
    }
}
