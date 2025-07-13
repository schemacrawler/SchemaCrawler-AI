/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.langchain4j;

import java.time.Duration;
import static java.util.Objects.requireNonNull;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import schemacrawler.tools.ai.chat.ChatOptions;
import schemacrawler.tools.command.aichat.langchain4j.AiModelFactoryUtility.AiModelFactory;
import us.fatehi.utility.property.PropertyName;

public class OpenAIModelFactory implements AiModelFactory {

  private final PropertyName aiProvider = new PropertyName("openai", "OpenAI");
  private final ChatOptions aiChatCommandOptions;

  public OpenAIModelFactory(final ChatOptions commandOptions) {
    aiChatCommandOptions = requireNonNull(commandOptions, "No AI Chat options provided");
  }

  @Override
  public boolean hasEmbeddingModel() {
    return true;
  }

  @Override
  public boolean isSupported() {
    if (!aiChatCommandOptions.aiProvider().equals(aiProvider.getName())) {
      return false;
    }
    final String model = aiChatCommandOptions.model();
    for (final OpenAiChatModelName modelName : OpenAiChatModelName.values()) {
      if (modelName.toString().equals(model)) {
        return true;
      }
    }
    return model.startsWith("gpt-");
  }

  @Override
  public ChatMemory newChatMemory() {
    return TokenWindowChatMemory.builder()
        .maxTokens(8_000, new OpenAiTokenCountEstimator(aiChatCommandOptions.model()))
        .build();
  }

  @Override
  public ChatModel newChatModel() {
    return OpenAiChatModel.builder()
        .apiKey(aiChatCommandOptions.apiKey())
        .modelName(aiChatCommandOptions.model())
        .temperature(0.2)
        .timeout(Duration.ofSeconds(aiChatCommandOptions.timeout()))
        // https://docs.langchain4j.dev/integrations/language-models/open-ai#structured-outputs-for-tools
        .strictTools(true)
        .logRequests(true)
        .logResponses(true)
        .build();
  }

  @Override
  public EmbeddingModel newEmbeddingModel() {
    final String embeddingModelName = OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL.toString();
    return OpenAiEmbeddingModel.builder()
        .apiKey(aiChatCommandOptions.apiKey())
        .modelName(embeddingModelName)
        .build();
  }

  @Override
  public String toString() {
    return aiProvider.toString();
  }
}
