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

package schemacrawler.tools.command.aichat.utility.lanchain4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.tool.ToolExecutor;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.string.StringFormat;

public class NoMetadataAiChatService implements AiChatService {

  private static final AiMessage TOOL_CALL_MEMORY_MESSAGE =
      AiMessage.from("(Information on tool calls is redacted for security purposes.)");

  private static final Logger LOGGER =
      Logger.getLogger(Langchain4JChatAssistant.class.getCanonicalName());

  private final ChatLanguageModel model;
  private final ChatMemory chatMemory;
  private final List<ToolSpecification> toolSpecifications;
  private final Map<String, ToolExecutor> toolExecutors;
  private final String metadataPriming;

  public NoMetadataAiChatService(
      final ChatLanguageModel model,
      final ChatMemory chatMemory,
      final Map<ToolSpecification, ToolExecutor> toolSpecificationsMap) {

    this.model = requireNonNull(model, "No chat language model provided");
    this.chatMemory = requireNonNull(chatMemory, "No chat language model provided");
    requireNonNull(toolSpecificationsMap, "No tool specifications map provided");

    toolSpecifications = new ArrayList<>(toolSpecificationsMap.keySet());

    toolExecutors = new HashMap<>();
    for (final Entry<ToolSpecification, ToolExecutor> toolSpecification :
        toolSpecificationsMap.entrySet()) {
      toolExecutors.put(toolSpecification.getKey().name(), toolSpecification.getValue());
    }

    metadataPriming = IOUtility.readResourceFully("/metadata-priming.txt");
  }

  /**
   * Send prompt to AI chat API and get completions.
   *
   * @param prompt Input prompt.
   */
  @Override
  public Response<AiMessage> chat(final String prompt) {
    try {
      chatMemory.add(SystemMessage.from(metadataPriming));
      chatMemory.add(UserMessage.from(prompt));

      final Response<AiMessage> responseMessage =
          model.generate(chatMemory.messages(), toolSpecifications);
      final TokenUsage tokenUsage = responseMessage.tokenUsage();
      LOGGER.log(Level.INFO, new StringFormat("%s", tokenUsage));

      final AiMessage aiMessage = responseMessage.content();
      final String response;
      if (aiMessage.hasToolExecutionRequests()) {
        final StringBuilder buffer = new StringBuilder();
        final List<ToolExecutionRequest> executionRequests = aiMessage.toolExecutionRequests();
        for (final ToolExecutionRequest toolExecutionRequest : executionRequests) {
          final ToolExecutor toolExecutor = toolExecutors.get(toolExecutionRequest.name());
          final String toolExecutionResult = toolExecutor.execute(toolExecutionRequest, null);
          buffer.append(toolExecutionResult);
        }
        chatMemory.add(TOOL_CALL_MEMORY_MESSAGE);
        response = buffer.toString();

        return Response.from(AiMessage.from(response), responseMessage.tokenUsage());
      }

      // If no tools need to be executed, return as-is
      chatMemory.add(aiMessage);
      return responseMessage;

    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, e, new StringFormat("Exception handling prompt:%n%s", prompt));
      e.printStackTrace(System.err);
      return Response.from(AiMessage.from("There was a problem. Please try again."));
    }
  }
}
