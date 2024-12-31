package us.fatehi.example;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutor;

public class SingleCompletion {

  interface Assistant {

    String chat(String userMessage);
  }

  private static final class SimpleToolExecutor implements ToolExecutor {
    @Override
    public String execute(final ToolExecutionRequest toolExecutionRequest, final Object memoryId) {
      System.out.printf(
          "id=%s, name=%s, args=%s%n",
          toolExecutionRequest.id(), toolExecutionRequest.name(), toolExecutionRequest.arguments());
      return "{'tables' = ['table1', 'table2']}";
    }
  }

  public static void main(final String[] args) throws Exception {

    final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    final ChatLanguageModel model =
        OpenAiChatModel.builder()
            .apiKey(OPENAI_API_KEY)
            .modelName(GPT_4_O_MINI)
            .strictTools(
                true) // https://docs.langchain4j.dev/integrations/language-models/open-ai#structured-outputs-for-tools
            .build();

    final List<ToolSpecification> toolSpecifications = AiChatUtility.newToolSpecifications();
    final ToolExecutor exeuctor = new SimpleToolExecutor();
    final Map<ToolSpecification, ToolExecutor> toolSpecificationsMap = new HashMap<>();
    for (final ToolSpecification toolSpecification : toolSpecifications) {
      toolSpecificationsMap.put(toolSpecification, exeuctor);
    }

    final Assistant assistant =
        AiServices.builder(Assistant.class)
            .chatLanguageModel(model)
            .tools(toolSpecificationsMap)
            .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
            .build();

    final String question = "List all tables in the database";
    final String answer = assistant.chat(question);
    System.out.println(answer);
  }
}
