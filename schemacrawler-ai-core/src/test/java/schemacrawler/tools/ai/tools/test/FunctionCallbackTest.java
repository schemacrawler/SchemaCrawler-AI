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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schemacrawler.ermodel.model.ERModel;
import schemacrawler.schema.Catalog;
import schemacrawler.test.utility.crawl.LightCatalogUtility;
import schemacrawler.tools.ai.tools.FunctionCallback;
import schemacrawler.tools.ai.tools.FunctionDefinition;
import schemacrawler.tools.ai.tools.FunctionExecutor;
import schemacrawler.tools.ai.tools.FunctionParameters;
import schemacrawler.tools.ai.tools.FunctionReturn;
import schemacrawler.tools.ai.tools.TextFunctionReturn;
import tools.jackson.databind.JsonNode;
import us.fatehi.test.utility.TestObjectUtility;
import us.fatehi.utility.property.PropertyName;

public class FunctionCallbackTest {

  public static record TestParameters(String param1) implements FunctionParameters {
    public TestParameters() {
      this(null);
    }
  }

  private Connection connection;
  private Catalog catalog;
  private ERModel erModel;

  @BeforeEach
  public void setupCatalog() {
    connection = TestObjectUtility.mockConnection();
    catalog = LightCatalogUtility.lightCatalog();
    erModel = TestObjectUtility.makeTestObject(ERModel.class);
  }

  @Test
  public void testConstructor() {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);

    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, catalog, erModel);
    assertThat(callback, is(notNullValue()));
  }

  @Test
  public void testConstructorWithNullDefinition() {
    assertThrows(NullPointerException.class, () -> new FunctionCallback<>(null, catalog, erModel));
  }

  @Test
  public void testExecute() throws Exception {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);
    final FunctionExecutor<TestParameters> executor = mock(FunctionExecutor.class);

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
  public void testExecuteCheckedException() throws Exception {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);
    final FunctionExecutor<TestParameters> executor = mock(FunctionExecutor.class);

    when(definition.getFunctionName()).thenReturn(new PropertyName("test-function"));
    when(definition.getParametersClass()).thenReturn(TestParameters.class);
    when(definition.newExecutor()).thenReturn(executor);
    // Throw a checked exception from call()
    when(executor.call()).thenThrow(new Exception("checked exception"));

    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, null, null);

    final Exception exception =
        assertThrows(
            schemacrawler.schemacrawler.exceptions.InternalRuntimeException.class,
            () -> callback.execute("{}", connection));
    assertThat(exception.getMessage(), containsString("Exception executing"));
    assertThat(exception.getMessage(), containsString("test-function"));
  }

  @Test
  public void testExecuteNoConnection() throws Exception {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);
    final FunctionExecutor<TestParameters> executor = mock(FunctionExecutor.class);
    final FunctionReturn expectedReturn = new TextFunctionReturn("result");

    when(definition.getFunctionName()).thenReturn(new PropertyName("test-function"));
    when(definition.getParametersClass()).thenReturn(TestParameters.class);
    when(definition.newExecutor()).thenReturn(executor);
    when(executor.usesConnection()).thenReturn(false);
    when(executor.call()).thenReturn(expectedReturn);

    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, null, null);
    final FunctionReturn actualReturn = callback.execute("{}", connection);

    assertThat(actualReturn, is(expectedReturn));
    verify(executor).configure(any(TestParameters.class));
    verify(executor).initialize();
    verify(executor, org.mockito.Mockito.never()).setConnection(any());
  }

  @Test
  public void testExecuteWithException() throws Exception {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);
    final FunctionExecutor<TestParameters> executor = mock(FunctionExecutor.class);

    when(definition.getFunctionName()).thenReturn(new PropertyName("test-function"));
    when(definition.getParametersClass()).thenReturn(TestParameters.class);
    when(definition.newExecutor()).thenReturn(executor);
    doThrow(new RuntimeException("test error")).when(executor).initialize();

    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, null, null);

    final RuntimeException exception =
        assertThrows(RuntimeException.class, () -> callback.execute("{}", connection));
    assertThat(exception.getMessage(), is("test error"));
  }

  @Test
  public void testExecuteWithNullConnection() {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);
    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, null, null);

    assertThrows(NullPointerException.class, () -> callback.execute("{}", null));
  }

  @Test
  public void testGetFunctionName() {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);
    final PropertyName propertyName = new PropertyName("test-function");
    when(definition.getFunctionName()).thenReturn(propertyName);

    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, null, null);
    assertThat(callback.getFunctionName(), is(propertyName));
  }

  @Test
  public void testInstantiateArgumentsFailure() throws Exception {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);
    final FunctionExecutor<TestParameters> executor = mock(FunctionExecutor.class);

    when(definition.getFunctionName()).thenReturn(new PropertyName("test-function"));
    when(definition.getParametersClass()).thenReturn(TestParameters.class);
    when(definition.newExecutor()).thenReturn(executor);
    when(executor.call()).thenReturn(new TextFunctionReturn("ok"));

    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, null, null);

    // This should trigger the catch block in instantiateArguments and use default
    // constructor
    final FunctionReturn result = callback.execute("invalid-json", connection);
    assertThat(result, is(notNullValue()));
    verify(executor).configure(any(TestParameters.class));
  }

  @Test
  public void testToCallObject() {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);
    when(definition.getFunctionName()).thenReturn(new PropertyName("test-function"));

    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, null, null);

    // Valid JSON
    final JsonNode node = callback.toCallObject("{\"param1\": \"value1\"}");
    assertThat(node.get("name").asString(), is("test-function"));
    assertThat(node.get("arguments").get("param1").asString(), is("value1"));

    // Blank arguments
    final JsonNode nodeBlank = callback.toCallObject("");
    assertThat(nodeBlank.get("arguments").isEmpty(), is(true));

    // Invalid JSON
    final JsonNode nodeInvalid = callback.toCallObject("invalid-json");
    assertThat(nodeInvalid.get("arguments").asString(), is("invalid-json"));
  }

  @Test
  public void testToString() {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);
    when(definition.getFunctionName()).thenReturn(new PropertyName("test-function"));

    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, null, null);
    final String toString = callback.toString();
    assertThat(toString, containsString("\"name\" : \"test-function\""));
  }
}
