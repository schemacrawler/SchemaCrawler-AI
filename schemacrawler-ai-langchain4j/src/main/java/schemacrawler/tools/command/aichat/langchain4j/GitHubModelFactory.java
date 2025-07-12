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
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.github.GitHubModelsChatModel;
import dev.langchain4j.model.github.GitHubModelsChatModelName;
import dev.langchain4j.model.github.GitHubModelsEmbeddingModel;
import schemacrawler.tools.command.aichat.langchain4j.AiModelFactoryUtility.AiModelFactory;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;
import us.fatehi.utility.property.PropertyName;

public class GitHubModelFactory implements AiModelFactory {

  private final PropertyName aiProvider = new PropertyName("github-models", "GitHub Models");
  private final AiChatCommandOptions aiChatCommandOptions;

  public GitHubModelFactory(final AiChatCommandOptions commandOptions) {
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
    for (final GitHubModelsChatModelName openAiChatModelName : GitHubModelsChatModelName.values()) {
      if (openAiChatModelName.toString().equals(model)) {
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
    return GitHubModelsChatModel.builder()
        .gitHubToken(aiChatCommandOptions.apiKey())
        .modelName(aiChatCommandOptions.model())
        .temperature(0.2)
        .timeout(Duration.ofSeconds(aiChatCommandOptions.timeout()))
        .build();
  }

  @Override
  public EmbeddingModel newEmbeddingModel() {
    final String embeddingModelName = "text-embedding-3-small";
    return GitHubModelsEmbeddingModel.builder()
        .gitHubToken(aiChatCommandOptions.apiKey())
        .modelName(embeddingModelName)
        .build();
  }

  @Override
  public String toString() {
    return aiProvider.toString();
  }
}
