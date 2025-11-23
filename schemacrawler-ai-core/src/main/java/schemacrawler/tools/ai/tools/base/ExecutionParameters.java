package schemacrawler.tools.ai.tools.base;

import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.options.Config;

public record ExecutionParameters(
    String command,
    Config additionalConfig,
    SchemaCrawlerOptions schemaCrawlerOptions,
    String outputFormat) {

  public ExecutionParameters(
      String command,
      Config additionalConfig,
      SchemaCrawlerOptions schemaCrawlerOptions,
      String outputFormat) {
    this.command = requireNotBlank(command, "Command not provided");

    // Can be null
    this.additionalConfig = additionalConfig;
    // Can be null
    this.schemaCrawlerOptions = schemaCrawlerOptions;

    final String outputFormatValue;
    if (isBlank(outputFormat)) {
      outputFormatValue = "text";
    } else {
      outputFormatValue = outputFormat.strip();
    }
    this.outputFormat = outputFormatValue;
  }

  public ExecutionParameters(
      String command, SchemaCrawlerOptions schemaCrawlerOptions, String outputFormat) {
    this(command, null, schemaCrawlerOptions, outputFormat);
  }
}
