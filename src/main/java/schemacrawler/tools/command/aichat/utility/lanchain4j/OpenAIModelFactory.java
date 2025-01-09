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

import java.time.Duration;
import static java.util.Objects.requireNonNull;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;
import schemacrawler.tools.command.aichat.utility.lanchain4j.AiModelFactoryUtility.AiModelFactory;

public class OpenAIModelFactory implements AiModelFactory {

  private static final String TEXT_EMBEDDING_MODEL = "text-embedding-3-small";

  private final AiChatCommandOptions aiChatCommandOptions;

  public OpenAIModelFactory(final AiChatCommandOptions commandOptions) {
    aiChatCommandOptions = requireNonNull(commandOptions, "No AI Chat options provided");
  }

  @Override
  public boolean isSupported() {
    final String model = aiChatCommandOptions.model();
    for (final OpenAiChatModelName openAiChatModelName : OpenAiChatModelName.values()) {
      if (openAiChatModelName.name().equals(model)) {
        return true;
      }
    }
    return model.startsWith("gpt-");
  }

  @Override
  public ChatLanguageModel newChatLanguageModel() {
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
  public ChatMemory newChatMemory() {
    return TokenWindowChatMemory.builder()
        .maxTokens(8_000, new OpenAiTokenizer(aiChatCommandOptions.model()))
        .build();
  }

  @Override
  public EmbeddingModel newEmbeddingModel() {
    requireNonNull(aiChatCommandOptions, "No AI Chat options provided");
    return OpenAiEmbeddingModel.builder()
        .apiKey(aiChatCommandOptions.apiKey())
        .modelName(TEXT_EMBEDDING_MODEL)
        .build();
  }
}
