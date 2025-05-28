package schemacrawler.tools.command.aichat.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.command.aichat.functions.json.DescribeTablesFunctionDefinition;
import schemacrawler.tools.command.aichat.functions.json.DescribeTablesFunctionParameters;

public class AbstractFunctionDefinitionTest {

  @Test
  public void properties() {
    final DescribeTablesFunctionDefinition functionDefinition =
        new DescribeTablesFunctionDefinition();

    final String functionName = "describe-tables";
    assertThat(functionDefinition.toString(), startsWith("function " + functionName));

    assertThat(functionDefinition.getName(), is(functionName));
    assertThat(
        functionDefinition.getDescription(),
        startsWith("Get the details and description of database tables or views"));
    assertThat(
        functionDefinition.getParametersClass().getSimpleName(),
        is(DescribeTablesFunctionParameters.class.getSimpleName()));
  }
}
