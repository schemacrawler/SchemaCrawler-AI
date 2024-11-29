package schemacrawler.tools.command.chatgpt.test;

import java.util.function.Function;
import org.junit.jupiter.api.Test;
import nl.jqno.equalsverifier.EqualsVerifier;
import schemacrawler.tools.command.chatgpt.FunctionParameters;
import schemacrawler.tools.command.chatgpt.FunctionReturn;
import schemacrawler.tools.command.chatgpt.functions.AbstractFunctionDefinition;

public class EqualsTest {

  public static class TestFunctionDefinition
      extends AbstractFunctionDefinition<FunctionParameters> {

    protected TestFunctionDefinition(
        final String name, final Class<FunctionParameters> parameters) {
      super(parameters);
    }

    @Override
    public Function<FunctionParameters, FunctionReturn> getExecutor() {
      return null;
    }

    @Override
    public String getDescription() {
      return "";
    }
  }

  @Test
  public void functionDefinition() {
    EqualsVerifier.forClass(TestFunctionDefinition.class)
        .withIgnoredFields("catalog", "connection")
        .withRedefinedSuperclass()
        .usingGetClass()
        .verify();
  }
}
