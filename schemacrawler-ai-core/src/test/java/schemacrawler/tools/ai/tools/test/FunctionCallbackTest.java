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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static schemacrawler.tools.ai.utility.JsonUtility.mapper;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;
import us.fatehi.utility.property.PropertyName;

public class FunctionCallbackTest {

  public static record TestParameters(String param1) implements FunctionParameters {
    public TestParameters() {
      this(null);
    }
  }

  public enum Mode {
    FIRST
  }

  public static record StructuredParameters(
      String camelCase, int count, Mode mode, List<Map<String, Object>> details)
      implements FunctionParameters {}

  private DatabaseConnectionSource connectionSource;
  private Catalog catalog;
  private ERModel erModel;

  @BeforeEach
  public void setupCatalog() {
    connectionSource = DatabaseConnectionSources.fromConnection(TestObjectUtility.mockConnection());
    catalog = LightCatalogUtility.lightCatalog();
    erModel = TestObjectUtility.makeTestObject(ERModel.class);
  }

  @Test
  public void testConstructor() {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);

    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, catalog, erModel, null);
    assertThat(callback, is(notNullValue()));
  }

  @Test
  public void testConstructorWithNullDefinition() {
    assertThrows(
        NullPointerException.class, () -> new FunctionCallback<>(null, catalog, erModel, null));
  }

  @Test
  public void testExecute() throws Exception {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);
    final FunctionExecutor<TestParameters> executor = mock(FunctionExecutor.class);

    final FunctionReturn expectedReturn = new TextFunctionReturn("result");

    when(definition.getFunctionName()).thenReturn(new PropertyName("test-function"));
    when(definition.getParametersClass()).thenReturn(TestParameters.class);
    when(definition.newParameters()).thenReturn(new TestParameters());
    when(definition.newExecutor()).thenReturn(executor);
    when(executor.usesConnection()).thenReturn(true);
    when(executor.call()).thenReturn(expectedReturn);

    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, catalog, erModel, null);
    final FunctionReturn actualReturn =
        callback.execute(mapper.readTree("{\"param1\": \"value1\"}"), connectionSource);

    assertThat(actualReturn, is(expectedReturn));
    verify(executor).configure(new TestParameters("value1"));
    verify(executor).initialize();
    verify(executor).setCatalog(catalog);
    verify(executor).setERModel(erModel);
    verify(executor).setConnectionSource(connectionSource);
  }

  @Test
  public void testExecuteCheckedException() throws Exception {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);
    final FunctionExecutor<TestParameters> executor = mock(FunctionExecutor.class);

    when(definition.getFunctionName()).thenReturn(new PropertyName("test-function"));
    when(definition.getParametersClass()).thenReturn(TestParameters.class);
    when(definition.newParameters()).thenReturn(new TestParameters());
    when(definition.newExecutor()).thenReturn(executor);
    // Throw a checked exception from call()
    when(executor.call()).thenThrow(new Exception("checked exception"));

    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, null, null, null);

    final Exception exception =
        assertThrows(
            schemacrawler.schemacrawler.exceptions.InternalRuntimeException.class,
            () -> callback.execute(mapper.createObjectNode(), connectionSource));
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
    when(definition.newParameters()).thenReturn(new TestParameters());
    when(definition.newExecutor()).thenReturn(executor);
    when(executor.usesConnection()).thenReturn(false);
    when(executor.call()).thenReturn(expectedReturn);

    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, null, null, null);
    final FunctionReturn actualReturn =
        callback.execute(mapper.createObjectNode(), connectionSource);

    assertThat(actualReturn, is(expectedReturn));
    verify(executor).configure(any(TestParameters.class));
    verify(executor).initialize();
    verify(executor, org.mockito.Mockito.never()).setConnectionSource(any());
  }

  @Test
  public void testExecuteWithException() throws Exception {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);
    final FunctionExecutor<TestParameters> executor = mock(FunctionExecutor.class);

    when(definition.getFunctionName()).thenReturn(new PropertyName("test-function"));
    when(definition.getParametersClass()).thenReturn(TestParameters.class);
    when(definition.newParameters()).thenReturn(new TestParameters());
    when(definition.newExecutor()).thenReturn(executor);
    doThrow(new RuntimeException("test error")).when(executor).initialize();

    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, null, null, null);

    final RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> callback.execute(mapper.createObjectNode(), connectionSource));
    assertThat(exception.getMessage(), is("test error"));
  }

  @Test
  public void testExecuteWithNullConnection() {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);
    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, null, null, null);

    assertThrows(
        NullPointerException.class, () -> callback.execute(mapper.createObjectNode(), null));
  }

  @Test
  public void testGetFunctionName() {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);
    final PropertyName propertyName = new PropertyName("test-function");
    when(definition.getFunctionName()).thenReturn(propertyName);

    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, null, null, null);
    assertThat(callback.getFunctionName(), is(propertyName));
  }

  @Test
  public void testInstantiateArgumentsFailure() throws Exception {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);
    final FunctionExecutor<TestParameters> executor = mock(FunctionExecutor.class);

    when(definition.getFunctionName()).thenReturn(new PropertyName("test-function"));
    when(definition.getParametersClass()).thenReturn(TestParameters.class);
    final TestParameters defaults = new TestParameters("default");
    when(definition.newParameters()).thenReturn(defaults);
    when(definition.newExecutor()).thenReturn(executor);
    when(executor.call()).thenReturn(new TextFunctionReturn("ok"));

    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, null, null, null);

    final FunctionReturn result =
        callback.execute(mapper.readTree("{\"param1\": {\"invalid\": true}}"), connectionSource);
    assertThat(result, is(notNullValue()));
    verify(executor).configure(defaults);
  }

  @Test
  public void testToCallObject() {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);
    when(definition.getFunctionName()).thenReturn(new PropertyName("test-function"));

    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, null, null, null);

    final JsonNode arguments = mapper.readTree("{\"param1\": \"value1\"}");
    final JsonNode node = callback.toCallObject(arguments);
    assertThat(node.get("name").asString(), is("test-function"));
    assertThat(node.get("arguments").get("param1").asString(), is("value1"));

    final JsonNode nodeBlank = callback.toCallObject(null);
    assertThat(nodeBlank.get("arguments").isEmpty(), is(true));
  }

  @Test
  public void structuredBindingUsesSnakeCaseAndPreservesArguments() throws Exception {
    final FunctionDefinition<StructuredParameters> definition = mock(FunctionDefinition.class);
    final FunctionExecutor<StructuredParameters> executor = mock(FunctionExecutor.class);
    final StructuredParameters defaults =
        new StructuredParameters("default", 7, Mode.FIRST, List.of());
    when(definition.getFunctionName()).thenReturn(new PropertyName("structured-function"));
    when(definition.getParametersClass()).thenReturn(StructuredParameters.class);
    when(definition.newParameters()).thenReturn(defaults);
    when(definition.newExecutor()).thenReturn(executor);
    when(executor.call()).thenReturn(new TextFunctionReturn("ok"));
    final FunctionCallback<StructuredParameters> callback =
        new FunctionCallback<>(definition, catalog, erModel, null);
    final List<JsonNode> inputs =
        List.of(
            mapper.readTree(
                """
                {"camel_case":"value","count":"2","mode":"first",
                 "details":[{"nested":{"values":[1,null,true]},"empty":""}]}
                """),
            mapper.readTree("{\"count\":null}"),
            mapper.readTree("{\"camelCase\":\"unsupported\"}"),
            mapper.readTree("{\"unknown_field\":true}"),
            mapper.readTree("{\"count\":{\"invalid\":true}}"));
    for (final JsonNode arguments : inputs) {
      final JsonNode before = arguments.deepCopy();
      callback.execute(arguments, connectionSource);
      assertThat(arguments, is(before));
      assertThat(callback.toCallObject(arguments).get("arguments"), is(before));
    }
    final ArgumentCaptor<StructuredParameters> configured =
        ArgumentCaptor.forClass(StructuredParameters.class);
    verify(executor, times(inputs.size())).configure(configured.capture());
    final StructuredParameters valid = configured.getAllValues().get(0);
    assertThat(valid.camelCase(), is("value"));
    assertThat(valid.count(), is(2));
    assertThat(valid.mode(), is(Mode.FIRST));
    assertThat(valid.details().get(0).get("empty"), is(""));
    assertThat(
        mapper.valueToTree(valid.details().get(0).get("nested")),
        is(inputs.get(0).get("details").get(0).get("nested")));
    assertThat(configured.getAllValues().get(1), is(new StructuredParameters(null, 0, null, null)));
    for (int index = 2; index < inputs.size(); index++) {
      assertThat(configured.getAllValues().get(index), is(defaults));
    }
  }

  @Test
  public void repeatedExecutionAndFailureLoggingDoNotMutateArguments() throws Exception {
    final FunctionDefinition<StructuredParameters> definition = mock(FunctionDefinition.class);
    final FunctionExecutor<StructuredParameters> first = mock(FunctionExecutor.class);
    final FunctionExecutor<StructuredParameters> second = mock(FunctionExecutor.class);
    when(definition.getFunctionName()).thenReturn(new PropertyName("structured-function"));
    when(definition.getParametersClass()).thenReturn(StructuredParameters.class);
    when(definition.newExecutor()).thenReturn(first, second);
    when(first.call()).thenReturn(new TextFunctionReturn("ok"));
    when(second.call()).thenThrow(new IllegalStateException("execution failure"));
    final FunctionCallback<StructuredParameters> callback =
        new FunctionCallback<>(definition, catalog, erModel, null);
    final JsonNode arguments =
        mapper.readTree("{\"details\":[{\"nested\":{\"values\":[1,null,true]}}]}");
    final JsonNode before = arguments.deepCopy();
    final Logger logger = Logger.getLogger(FunctionCallback.class.getCanonicalName());
    final Level originalLevel = logger.getLevel();
    logger.setLevel(Level.FINER);
    try {
      assertThat(callback.toCallObject(arguments).get("arguments"), is(before));
      callback.execute(arguments, connectionSource);
      assertThat(arguments, is(before));
      assertThrows(
          IllegalStateException.class, () -> callback.execute(arguments, connectionSource));
      assertThat(arguments, is(before));
      verify(first).initialize();
      verify(second).initialize();
    } finally {
      logger.setLevel(originalLevel);
    }
  }

  @Test
  public void testToString() {
    final FunctionDefinition<TestParameters> definition = mock(FunctionDefinition.class);
    when(definition.getFunctionName()).thenReturn(new PropertyName("test-function"));

    final FunctionCallback<TestParameters> callback =
        new FunctionCallback<>(definition, null, null, null);
    final String toString = callback.toString();
    assertThat(toString, containsString("\"name\" : \"test-function\""));
  }
}
