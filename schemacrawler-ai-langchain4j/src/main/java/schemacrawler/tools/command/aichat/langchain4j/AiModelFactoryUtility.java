/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.tools.command.aichat.langchain4j;

import static java.util.Objects.requireNonNull;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import schemacrawler.tools.ai.chat.ChatOptions;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class AiModelFactoryUtility {

  interface AiModelFactory {

    boolean hasEmbeddingModel();

    boolean isSupported();

    ChatMemory newChatMemory();

    ChatModel newChatModel();

    EmbeddingModel newEmbeddingModel();
  }

  public static AiModelFactory chooseAiModelFactory(final ChatOptions aiChatCommandOptions) {
    requireNonNull(aiChatCommandOptions, "No AI Chat options provided");
    final AiModelFactory[] modelFactories = {
      new OpenAIModelFactory(aiChatCommandOptions), new AnthropicModelFactory(aiChatCommandOptions)
    };
    for (final AiModelFactory aiModelFactory : modelFactories) {
      if (aiModelFactory.isSupported()) {
        return aiModelFactory;
      }
    }
    return null;
  }

  private AiModelFactoryUtility() {
    // Prevent instantiation
  }
}
