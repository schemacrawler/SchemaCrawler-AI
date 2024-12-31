/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package us.fatehi.example;

import java.io.PrintStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import static java.util.Objects.requireNonNull;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonEnumSchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchemaElement;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import io.github.sashirestela.openai.common.function.FunctionCall;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.AssistantMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.ToolMessage;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.aichat.FunctionDefinition;
import schemacrawler.tools.command.aichat.FunctionDefinition.FunctionType;
import schemacrawler.tools.command.aichat.FunctionParameters;
import schemacrawler.tools.command.aichat.FunctionReturn;
import schemacrawler.tools.command.aichat.functions.FunctionDefinitionRegistry;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
public class AiChatUtility {

  private static final Logger LOGGER = Logger.getLogger(AiChatUtility.class.getCanonicalName());

  public static <P extends FunctionParameters> String execute(
      final FunctionCall functionCall, final Catalog catalog, final Connection connection) {
    requireNonNull(functionCall, "No function call provided");

    // Look up function definition
    final FunctionDefinition<P> functionDefinitionToCall =
        (FunctionDefinition<P>) lookupFunctionDefinition(functionCall);
    if (functionDefinitionToCall == null) {
      return "";
    }

    // Build parameters
    final Class<P> parametersClass = functionDefinitionToCall.getParametersClass();
    final P parameters = instantiateParameters(functionCall, parametersClass);

    // Execute function
    FunctionReturn functionReturn;
    try {
      final schemacrawler.tools.command.aichat.FunctionExecutor<P> functionExecutor =
          functionDefinitionToCall.newExecutor();
      functionExecutor.configure(parameters);
      functionExecutor.initialize();
      functionExecutor.setCatalog(catalog);
      if (functionExecutor.usesConnection()) {
        functionExecutor.setConnection(connection);
      }
      functionReturn = functionExecutor.call();
      return functionReturn.get();
    } catch (final Exception e) {
      LOGGER.log(
          Level.INFO,
          e,
          new StringFormat(
              "Could not call function with arguments: %s(%s)",
              functionDefinitionToCall, functionCall.getArguments()));
      return "";
    }
  }

  public static List<ToolSpecification> newToolSpecifications() throws Exception {

    final List<ToolSpecification> chatFunctions = new ArrayList<>();
    for (final FunctionDefinition<?> functionDefinition :
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry().getFunctionDefinitions()) {
      if (functionDefinition.getFunctionType() != FunctionType.USER) {
        continue;
      }

      final Class<?> parametersClass = functionDefinition.getParametersClass();
      final Map<String, JsonNode> jsonSchema = jsonSchema(parametersClass);
      final Map<String, JsonSchemaElement> properties = toProperties(jsonSchema);
      final JsonObjectSchema parameters = JsonObjectSchema.builder().properties(properties).build();

      final ToolSpecification toolSpecification =
          ToolSpecification.builder()
              .name(functionDefinition.getName())
              .description(functionDefinition.getDescription())
              .parameters(parameters)
              .build();
      chatFunctions.add(toolSpecification);
    }
    return chatFunctions;
  }

  /**
   * Print AI chat API response.
   *
   * @param completions Chat completions
   * @param out Output stream to print to
   */
  public static void printResponse(final List<ChatMessage> completions, final PrintStream out) {
    requireNonNull(out, "No ouput stream provided");
    requireNonNull(completions, "No completions provided");
    for (final ChatMessage chatMessage : completions) {
      if (chatMessage instanceof final ToolMessage toolMessage) {
        out.println(toolMessage.getContent());
      }
      if (chatMessage instanceof final AssistantMessage assistantMessage) {
        out.println(assistantMessage.getContent());
      }
    }
  }

  // Helper methods to convert properties and required fields
  private static Map<String, Object> convertProperties(final JsonNode propertiesNode) {
    final Map<String, Object> properties = new HashMap<>();
    propertiesNode.fields();
    for (final Iterator<Entry<String, JsonNode>> iterator = propertiesNode.fields();
        iterator.hasNext(); ) {
      final Entry<String, JsonNode> property = iterator.next();
      properties.put(property.getKey(), property.getValue());
    }
    return properties;
  }

  private static <P extends FunctionParameters> P instantiateParameters(
      final FunctionCall functionCall, final Class<P> parametersClass) {
    final P parameters;
    final ObjectMapper objectMapper = new ObjectMapper();
    try {
      parameters = objectMapper.readValue(functionCall.getArguments(), parametersClass);
      LOGGER.log(Level.FINE, String.valueOf(parameters));
    } catch (final Exception e) {
      LOGGER.log(
          Level.INFO,
          e,
          new StringFormat(
              "Function parameters could not be instantiated: %s(%s)",
              parametersClass.getName(), functionCall.getArguments()));
      return null;
    }
    return parameters;
  }

  private static Map<String, JsonNode> jsonSchema(final Class<?> parametersClass) throws Exception {
    final ObjectMapper mapper = new ObjectMapper();
    final JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
    final JsonSchema schema = schemaGen.generateSchema(parametersClass);
    final JsonNode schemaNode = mapper.valueToTree(schema);
    final JsonNode properties = schemaNode.get("properties");
    final Set<Entry<String, JsonNode>> namedProperties;
    if (properties == null) {
      namedProperties = new HashSet<>();
    } else {
      namedProperties = properties.properties();
    }
    final Map<String, JsonNode> propertiesMap = new HashMap<>();
    for (final Entry<String, JsonNode> entry : namedProperties) {
      propertiesMap.put(entry.getKey(), entry.getValue());
    }
    return propertiesMap;
  }

  private static FunctionDefinition<?> lookupFunctionDefinition(final FunctionCall functionCall) {
    FunctionDefinition<?> functionDefinitionToCall = null;
    for (final FunctionDefinition<?> functionDefinition :
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry().getFunctionDefinitions()) {
      if (functionDefinition.getFunctionType() != FunctionType.USER) {
        continue;
      }
      if (functionDefinition.getName().equals(functionCall.getName())) {
        functionDefinitionToCall = functionDefinition;
        break;
      }
    }
    if (functionDefinitionToCall == null) {
      LOGGER.log(
          Level.INFO,
          new StringFormat(
              "Function not found: %s(%s)", functionCall.getName(), functionCall.getArguments()));
      return null;
    }
    return functionDefinitionToCall;
  }

  private static Map<String, JsonSchemaElement> toProperties(
      final Map<String, JsonNode> mapJsonSchema) {
    final Map<String, JsonSchemaElement> properties = new HashMap<>();
    for (final Entry<String, JsonNode> entry : mapJsonSchema.entrySet()) {
      final String propertyName = entry.getKey();
      final JsonNode propertyNode = entry.getValue();
      final String type = propertyNode.get("type").asText();
      final String description = propertyNode.get("description").asText();
      final JsonNode enumNode = propertyNode.get("enum");
      JsonSchemaElement jsonSchemaElement;
      if ("string".equals(type)) {
        if (enumNode == null) {
          jsonSchemaElement = JsonStringSchema.builder().description(description).build();
        } else {
          final List<String> enumValues = new ArrayList<>();
          enumNode.elements().forEachRemaining(node -> enumValues.add(node.asText()));
          jsonSchemaElement =
              JsonEnumSchema.builder().description(description).enumValues(enumValues).build();
        }
        properties.put(propertyName, jsonSchemaElement);
      }
    }
    return properties;
  }

  private AiChatUtility() {
    // Prevent instantiation
  }
}
