package schemacrawler.tools.command.aichat.test.utility;

import java.sql.Connection;
import java.util.UUID;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionReturnType;
import schemacrawler.tools.ai.tools.NoParameters;
import us.fatehi.utility.property.PropertyName;

public class TestTextFunction implements FunctionDefinition<NoParameters> {

  @Override
  public String getDescription() {
    return """
    Test text function.
    """
        .stripIndent()
        .replace("\n", " ")
        .trim();
  }

  @Override
  public FunctionReturnType getFunctionReturnType() {
    return FunctionReturnType.TEXT;
  }

  @Override
  public String getName() {
    return this.getClass().getSimpleName();
  }

  @Override
  public Class<NoParameters> getParametersClass() {
    return NoParameters.class;
  }

  @Override
  public FunctionExecutor newExecutor() {
    return new FunctionExecutor() {

      @Override
      public Object call() throws Exception {
        throw new UnsupportedOperationException();
      }

      @Override
      public void configure(final Object parameters) {
        throw new UnsupportedOperationException();
      }

      @Override
      public Catalog getCatalog() {
        throw new UnsupportedOperationException();
      }

      @Override
      public PropertyName getCommandName() {
        throw new UnsupportedOperationException();
      }

      @Override
      public Connection getConnection() {
        throw new UnsupportedOperationException();
      }

      @Override
      public String getDescription() {
        throw new UnsupportedOperationException();
      }

      @Override
      public UUID getExecutorInstanceId() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public void initialize() {
        throw new UnsupportedOperationException();
      }

      @Override
      public void setCatalog(final Catalog catalog) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void setConnection(final Connection connection) {
        throw new UnsupportedOperationException();
      }
    };
  }
}
