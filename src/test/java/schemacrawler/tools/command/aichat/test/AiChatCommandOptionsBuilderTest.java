package schemacrawler.tools.command.aichat.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptionsBuilder;
import schemacrawler.tools.options.Config;

public class AiChatCommandOptionsBuilderTest {

  @Test
  public void aiChatCommandOptionsBuilderApiKey() {

    assertThrows(
        IllegalArgumentException.class, () -> AiChatCommandOptionsBuilder.builder().toOptions());

    final AiChatCommandOptionsBuilder optionsBuilder =
        AiChatCommandOptionsBuilder.builder().withApiKey("api-key");
    assertThat(optionsBuilder.toOptions().getApiKey(), is("api-key"));
    assertThat(optionsBuilder.toOptions().getModel(), startsWith("gpt-4o-mini"));

    optionsBuilder.withApiKey(null);
    assertThat(optionsBuilder.toOptions().getApiKey(), is("api-key"));
    assertThat(optionsBuilder.toOptions().getModel(), startsWith("gpt-4o-mini"));

    optionsBuilder.withApiKey("\t");
    assertThat(optionsBuilder.toOptions().getApiKey(), is("api-key"));
    assertThat(optionsBuilder.toOptions().getModel(), startsWith("gpt-4o-mini"));

    optionsBuilder.withApiKey("new-api-key");
    assertThat(optionsBuilder.toOptions().getApiKey(), is("new-api-key"));
    assertThat(optionsBuilder.toOptions().getModel(), startsWith("gpt-4o-mini"));
  }

  @Test
  public void aiChatCommandOptionsBuilderContext() {

    final AiChatCommandOptionsBuilder optionsBuilder =
        AiChatCommandOptionsBuilder.builder().withApiKey("api-key");

    assertThat(optionsBuilder.toOptions().getContext(), is(10));
    optionsBuilder.withContext(20);
    assertThat(optionsBuilder.toOptions().getContext(), is(20));
    optionsBuilder.withContext(0);
    assertThat(optionsBuilder.toOptions().getContext(), is(10));
    optionsBuilder.withContext(500);
    assertThat(optionsBuilder.toOptions().getContext(), is(10));
    optionsBuilder.withContext(-2);
    assertThat(optionsBuilder.toOptions().getContext(), is(10));
  }

  @Test
  public void aiChatCommandOptionsBuilderModel() {

    assertThrows(
        IllegalArgumentException.class, () -> AiChatCommandOptionsBuilder.builder().toOptions());

    final AiChatCommandOptionsBuilder optionsBuilder =
        AiChatCommandOptionsBuilder.builder().withApiKey("api-key");

    optionsBuilder.withModel(null);
    assertThat(optionsBuilder.toOptions().getApiKey(), is("api-key"));
    assertThat(optionsBuilder.toOptions().getModel(), startsWith("gpt-4o-mini"));

    optionsBuilder.withModel("\t");
    assertThat(optionsBuilder.toOptions().getApiKey(), is("api-key"));
    assertThat(optionsBuilder.toOptions().getModel(), startsWith("gpt-4o-mini"));

    optionsBuilder.withModel("new-model");
    assertThat(optionsBuilder.toOptions().getApiKey(), is("api-key"));
    assertThat(optionsBuilder.toOptions().getModel(), is("new-model"));
  }

  @Test
  public void aiChatCommandOptionsBuilderTimeout() {

    final AiChatCommandOptionsBuilder optionsBuilder =
        AiChatCommandOptionsBuilder.builder().withApiKey("api-key");

    assertThat(optionsBuilder.toOptions().getTimeout(), is(10));
    optionsBuilder.withTimeout(20);
    assertThat(optionsBuilder.toOptions().getTimeout(), is(20));
    optionsBuilder.withTimeout(0);
    assertThat(optionsBuilder.toOptions().getTimeout(), is(0));
    optionsBuilder.withTimeout(500);
    assertThat(optionsBuilder.toOptions().getTimeout(), is(10));
    optionsBuilder.withTimeout(-2);
    assertThat(optionsBuilder.toOptions().getTimeout(), is(10));
  }

  @Test
  public void aiChatCommandOptionsBuilderUseMetadata() {

    final AiChatCommandOptionsBuilder optionsBuilder =
        AiChatCommandOptionsBuilder.builder().withApiKey("api-key");

    assertThat(optionsBuilder.toOptions().isUseMetadata(), is(false));
    optionsBuilder.withUseMetadata(true);
    assertThat(optionsBuilder.toOptions().isUseMetadata(), is(true));
    optionsBuilder.withUseMetadata(false);
    assertThat(optionsBuilder.toOptions().isUseMetadata(), is(false));
  }

  @Test
  public void fromConfig() {
    Config config;

    config = new Config();
    config.put("api-key", "api-key");
    final AiChatCommandOptions options =
        AiChatCommandOptionsBuilder.builder().fromConfig(config).toOptions();
    assertThat(options.getApiKey(), is("api-key"));
    assertThat(options.getModel(), startsWith("gpt-4o-mini"));

    // Have both keys
    config = new Config();
    config.put("api-key", "api-key");
    config.put("api-key:env", "api-key-env");
    final AiChatCommandOptions options2 =
        AiChatCommandOptionsBuilder.builder().fromConfig(config).toOptions();
    assertThat(options2.getApiKey(), is("api-key"));
    assertThat(options2.getModel(), startsWith("gpt-4o-mini"));

    config = new Config();
    config.put("api-key:env", "api-key-env");
    System.setProperty("api-key-env", "api-key");
    final AiChatCommandOptions options3 =
        AiChatCommandOptionsBuilder.builder().fromConfig(config).toOptions();
    assertThat(options3.getApiKey(), is("api-key"));
    assertThat(options3.getModel(), startsWith("gpt-4o-mini"));

    // Have both keys, with api-key blank
    config = new Config();
    config.put("api-key", "\t");
    config.put("api-key:env", "api-key-env");
    final AiChatCommandOptions options4 =
        AiChatCommandOptionsBuilder.builder().fromConfig(config).toOptions();
    assertThat(options4.getApiKey(), is("api-key"));
    assertThat(options4.getModel(), startsWith("gpt-4o-mini"));

    // No value for environmental variable
    final Config config1 = new Config();
    config1.put("api-key:env", "\t");
    assertThrows(
        IllegalArgumentException.class,
        () -> AiChatCommandOptionsBuilder.builder().fromConfig(config1).toOptions());

    // Null config
    assertThrows(
        IllegalArgumentException.class,
        () -> AiChatCommandOptionsBuilder.builder().fromConfig(null).toOptions());
  }

  @Test
  public void fromOptions() {
    final AiChatCommandOptions options =
        AiChatCommandOptionsBuilder.builder().withApiKey("api-key").withModel("model").toOptions();
    final AiChatCommandOptionsBuilder optionsBuilder =
        AiChatCommandOptionsBuilder.builder().fromOptions(options);
    assertThat(optionsBuilder.toOptions().getApiKey(), is("api-key"));
    assertThat(optionsBuilder.toOptions().getModel(), is("model"));

    // With null options
    assertThrows(
        IllegalArgumentException.class,
        () -> AiChatCommandOptionsBuilder.builder().fromOptions(null).toOptions());
  }

  @Test
  public void toConfig() {
    final AiChatCommandOptionsBuilder builder = AiChatCommandOptionsBuilder.builder();
    assertThrows(UnsupportedOperationException.class, () -> builder.toConfig());
  }
}
