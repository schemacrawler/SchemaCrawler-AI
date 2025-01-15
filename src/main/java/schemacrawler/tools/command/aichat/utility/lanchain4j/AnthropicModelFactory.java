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
import dev.langchain4j.exception.UnsupportedFeatureException;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;
import schemacrawler.tools.command.aichat.utility.lanchain4j.AiModelFactoryUtility.AiModelFactory;
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
  public ChatLanguageModel newChatLanguageModel() {
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
  public ChatMemory newChatMemory() {
    return MessageWindowChatMemory.withMaxMessages(aiChatCommandOptions.context());
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
