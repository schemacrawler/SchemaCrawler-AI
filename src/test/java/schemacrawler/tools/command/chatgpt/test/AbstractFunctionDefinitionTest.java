package schemacrawler.tools.command.chatgpt.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.KebabCaseStrategy;
import schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionDefinition;
import schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters;

public class AbstractFunctionDefinitionTest {

  @Test
  public void properties() {
    final TableDecriptionFunctionDefinition functionDefinition =
        new TableDecriptionFunctionDefinition();

    final String functionName =
        new KebabCaseStrategy().translate(TableDecriptionFunctionDefinition.class.getSimpleName());
    final String functionParameters =
        new KebabCaseStrategy().translate(TableDecriptionFunctionParameters.class.getSimpleName());
    assertThat(
        functionDefinition.toString(),
        startsWith("function " + functionName + "(" + functionParameters + ")"));

    assertThat(functionDefinition.getName(), is(functionName));
    assertThat(
        functionDefinition.getDescription(),
        is(
            "Gets the details and description of database tables or views, including columns, foreign keys, indexes and triggers."));
    assertThat(
        functionDefinition.getParametersClass().getSimpleName(),
        is("TableDecriptionFunctionParameters"));
  }
}
