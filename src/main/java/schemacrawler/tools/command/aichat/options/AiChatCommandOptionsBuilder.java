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

package schemacrawler.tools.command.aichat.options;

import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schemacrawler.OptionsBuilder;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigOptionsBuilder;
import us.fatehi.utility.PropertiesUtility;

public final class AiChatCommandOptionsBuilder
    implements OptionsBuilder<AiChatCommandOptionsBuilder, AiChatCommandOptions>,
        ConfigOptionsBuilder<AiChatCommandOptionsBuilder, AiChatCommandOptions> {

  private static final int DEFAULT_CONTEXT = 10;
  private static final int DEFAULT_TIMEOUT = 60;

  public static AiChatCommandOptionsBuilder builder() {
    return new AiChatCommandOptionsBuilder();
  }

  private String aiProvider;
  private String apiKey;
  private String model;
  private int timeout;
  private int context;
  private boolean useMetadata;

  private AiChatCommandOptionsBuilder() {
    aiProvider = "openai";
    model = "gpt-4o-mini";
    context = DEFAULT_CONTEXT;
    timeout = DEFAULT_TIMEOUT;
  }

  @Override
  public AiChatCommandOptionsBuilder fromConfig(final Config config) {
    if (config != null) {
      aiProvider = config.getStringValue("ai-provider", aiProvider);
      apiKey = getApiKey(config);
      model = config.getStringValue("model", model);
      timeout = config.getIntegerValue("timeout", DEFAULT_TIMEOUT);
      context = config.getIntegerValue("context", DEFAULT_CONTEXT);
      useMetadata = config.getBooleanValue("use-metadata");
    }

    return this;
  }

  @Override
  public AiChatCommandOptionsBuilder fromOptions(final AiChatCommandOptions options) {
    if (options != null) {
      aiProvider = options.aiProvider();
      apiKey = options.apiKey();
      model = options.model();
      timeout = options.timeout();
      context = options.context();
      useMetadata = options.useMetadata();
    }
    return this;
  }

  @Override
  public Config toConfig() {
    // Not implemented, since we cannot (should not) write API key to config
    throw new UnsupportedOperationException();
  }

  @Override
  public AiChatCommandOptions toOptions() {
    return new AiChatCommandOptions(aiProvider, apiKey, model, timeout, context, useMetadata);
  }

  /**
   * Use the provided AI provider is it is not blank.
   *
   * @param aiProvider AI provider.
   * @return Self.
   */
  public AiChatCommandOptionsBuilder withAiProvider(final String aiProvider) {
    if (!isBlank(aiProvider)) {
      this.aiProvider = aiProvider;
    }
    return this;
  }

  /**
   * Use the provided OpenAI API key is it is not blank.
   *
   * @param apiKey OpenAI API key.
   * @return Self.
   */
  public AiChatCommandOptionsBuilder withApiKey(final String apiKey) {
    if (!isBlank(apiKey)) {
      this.apiKey = apiKey;
    }
    return this;
  }

  /**
   * Use the provided context setting.
   *
   * @param context Chat context to keep.
   * @return Self.
   */
  public AiChatCommandOptionsBuilder withContext(final int context) {
    this.context = context;
    return this;
  }

  /**
   * Use the provided AI chat model is it is not blank.
   *
   * @param model AI chat model.
   * @return Self.
   */
  public AiChatCommandOptionsBuilder withModel(final String model) {
    if (!isBlank(model)) {
      this.model = model;
    }
    return this;
  }

  /**
   * Use the provided time out setting.
   *
   * @param timeout Request time-out in seconds.
   * @return Self.
   */
  public AiChatCommandOptionsBuilder withTimeout(final int timeout) {
    this.timeout = timeout;
    return this;
  }

  /**
   * Allow sharing of database metadata with OpenAI to enhance chat responses.
   *
   * @param useMetadata Whether to share database metadata with OpenAI to enhance chat responses.
   * @return Self.
   */
  public AiChatCommandOptionsBuilder withUseMetadata(final boolean useMetadata) {
    this.useMetadata = useMetadata;
    return this;
  }

  private String getApiKey(final Config config) {
    String apiKey = config.getStringValue("api-key", null);
    if (isBlank(apiKey)) {
      final String apikeyVar = config.getStringValue("api-key:env", "OPENAI_API_KEY");
      if (!isBlank(apikeyVar)) {
        apiKey = PropertiesUtility.getSystemConfigurationProperty(apikeyVar, null);
      }
    }
    return apiKey;
  }
}
