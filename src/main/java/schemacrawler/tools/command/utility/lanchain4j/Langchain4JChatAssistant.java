package schemacrawler.tools.command.utility.lanchain4j;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutor;
import schemacrawler.tools.command.aichat.ChatAssistant;

public class Langchain4JChatAssistant implements ChatAssistant {

  private final Assistant assistant;

  interface Assistant {
    String chat(String prompt);
  }

  public Langchain4JChatAssistant() throws Exception {

    final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    final ChatLanguageModel model =
        OpenAiChatModel.builder()
            .apiKey(OPENAI_API_KEY)
            .modelName(GPT_4_O_MINI)
            .strictTools(
                true) // https://docs.langchain4j.dev/integrations/language-models/open-ai#structured-outputs-for-tools
            .build();

    final List<ToolSpecification> toolSpecifications = Langchain4JUtility.toolsList();
    final ToolExecutor exeuctor = new Langchain4JToolExecutor();
    final Map<ToolSpecification, ToolExecutor> toolSpecificationsMap = new HashMap<>();
    for (final ToolSpecification toolSpecification : toolSpecifications) {
      toolSpecificationsMap.put(toolSpecification, exeuctor);
    }

    assistant =
        AiServices.builder(Assistant.class)
            .chatLanguageModel(model)
            .tools(toolSpecificationsMap)
            .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
            .build();
  }

  @Override
  public void close() throws Exception {}

  @Override
  public String chat(String prompt) {
    return assistant.chat(prompt);
  }

  @Override
  public boolean shouldExit() {
    return false;
  }
}
