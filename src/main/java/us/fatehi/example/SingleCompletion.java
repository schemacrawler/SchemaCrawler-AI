package us.fatehi.example;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutor;

public class SingleCompletion {

  interface Assistant {

    String chat(String userMessage);
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

    final List<ToolSpecification> toolSpecifications = Langchain4JUtility.toolsList();
    final ToolExecutor exeuctor = new AiChatToolExecutor();
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

    EmbeddingModel embeddingModel =
        OpenAiEmbeddingModel.builder()
            .apiKey(OPENAI_API_KEY)
            .modelName("text-embedding-3-small")
            .build();

    String text = "Your text here";
    Response<Embedding> response = embeddingModel.embed(text);

    Embedding embedding = response.content();
    System.out.println("Embedding: " + embedding);
  }
}
