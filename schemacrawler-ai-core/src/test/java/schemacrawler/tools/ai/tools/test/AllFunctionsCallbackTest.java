/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: BUSL-1.1
 */

package schemacrawler.tools.ai.tools.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.ai.tools.FunctionCallback;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionDefinitionRegistry;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.TextFunctionReturn;
import schemacrawler.tools.ai.tools.test.FunctionCallbackTest.TestParameters;
import schemacrawler.tools.ai.utility.EmptyFactory;
import us.fatehi.utility.property.PropertyName;

public class AllFunctionsCallbackTest {

  private Catalog catalog;
  private ERModel erModel;

  @BeforeEach
  public void setupCatalog() {
    catalog = EmptyFactory.createEmptyCatalog(null);
    erModel = EmptyFactory.createEmptyERModel();
  }

  @Test
  @Disabled
  public void testExecute() throws Exception {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);
    final FunctionExecutor<TestParameters> executor = mock(FunctionExecutor.class);

    final Connection connection = mock(Connection.class);
    final FunctionReturn expectedReturn = new TextFunctionReturn("result");

    when(definition.getFunctionName()).thenReturn(new PropertyName("test-function"));
    when(definition.getParametersClass()).thenReturn(TestParameters.class);
    when(definition.newExecutor()).thenReturn(executor);
    when(executor.usesConnection()).thenReturn(true);
    when(executor.call()).thenReturn(expectedReturn);

    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, catalog, erModel);
    final FunctionReturn actualReturn = callback.execute("{\"param1\": \"value1\"}", connection);

    assertThat(actualReturn, is(expectedReturn));
    verify(executor).configure(any(TestParameters.class));
    verify(executor).initialize();
    verify(executor).setCatalog(catalog);
    verify(executor).setERModel(erModel);
    verify(executor).setConnection(connection);
  }

  @Test
  public void testInstantiateInvalidArguments() throws Exception {
    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
    final Collection<FunctionDefinition<?>> functionDefinitions = registry.getFunctionDefinitions();
    for (final FunctionDefinition<?> definition : functionDefinitions) {
      final Connection connection = mock(Connection.class);
      final FunctionCallback<?> callback = new FunctionCallback<>(definition, catalog, erModel);
      final Exception exception =
          assertThrows(
              Exception.class,
              () -> callback.execute("invalid-json", connection),
              definition.getName());
      assertThat(exception.getMessage(), containsString("in an error state"));
    }
  }
}
