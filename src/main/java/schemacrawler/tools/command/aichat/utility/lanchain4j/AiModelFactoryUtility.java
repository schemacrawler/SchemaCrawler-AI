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

import static java.util.Objects.requireNonNull;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class AiModelFactoryUtility {

  interface AiModelFactory {

    boolean isSupported();

    ChatLanguageModel newChatLanguageModel();

    ChatMemory newChatMemory();

    EmbeddingModel newEmbeddingModel();
  }

  public static AiModelFactory chooseAiModelFactory(
      final AiChatCommandOptions aiChatCommandOptions) {
    requireNonNull(aiChatCommandOptions, "No AI Chat options provided");
    final AiModelFactory[] modelFactories = {new OpenAIModelFactory(aiChatCommandOptions)};
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