package schemacrawler.tools.command.aichat.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.command.aichat.functions.json.DescribeTablesFunctionDefinition;

public class AbstractFunctionDefinitionTest {

  @Test
  public void properties() {
    final DescribeTablesFunctionDefinition functionDefinition =
        new DescribeTablesFunctionDefinition();

    final String functionName = "table-decription";
    assertThat(functionDefinition.toString(), startsWith("function " + functionName));

    assertThat(functionDefinition.getName(), is(functionName));
    assertThat(
        functionDefinition.getDescription(),
        startsWith("Get the details and description of database tables or views"));
    assertThat(
        functionDefinition.getParametersClass().getSimpleName(),
        is("TableDecriptionFunctionParameters"));
  }
}
