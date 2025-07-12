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
import dev.langchain4j.exception.UnsupportedFeatureException;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import schemacrawler.tools.command.aichat.langchain4j.AiModelFactoryUtility.AiModelFactory;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;
import us.fatehi.utility.property.PropertyName;

public class AnthropicModelFactory implements AiModelFactory {

  private final PropertyName aiProvider = new PropertyName("anthropic", "Anthropic");
  private final AiChatCommandOptions aiChatCommandOptions;

  public AnthropicModelFactory(final AiChatCommandOptions commandOptions) {
    aiChatCommandOptions = requireNonNull(commandOptions, "No AI Chat options provided");
  }

  @Override
  public boolean hasEmbeddingModel() {
    return false;
  }

  @Override
  public boolean isSupported() {
    if (!aiChatCommandOptions.aiProvider().equals(aiProvider.getName())) {
      return false;
    }
    final String model = aiChatCommandOptions.model();
    for (final AnthropicChatModelName modelName : AnthropicChatModelName.values()) {
      if (modelName.toString().equals(model)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public ChatMemory newChatMemory() {
    return MessageWindowChatMemory.withMaxMessages(aiChatCommandOptions.context());
  }

  @Override
  public ChatModel newChatModel() {
    return AnthropicChatModel.builder()
        .apiKey(aiChatCommandOptions.apiKey())
        .modelName(aiChatCommandOptions.model())
        .temperature(0.2)
        .timeout(Duration.ofSeconds(aiChatCommandOptions.timeout()))
        .logRequests(true)
        .logResponses(true)
        .build();
  }

  @Override
  public EmbeddingModel newEmbeddingModel() {
    throw new UnsupportedFeatureException("Anthropic does not have embedding models");
  }

  @Override
  public String toString() {
    return aiProvider.toString();
  }
}
