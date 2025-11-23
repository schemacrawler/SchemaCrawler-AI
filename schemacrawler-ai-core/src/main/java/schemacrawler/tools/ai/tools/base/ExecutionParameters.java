package schemacrawler.tools.ai.tools.base;

import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import schemacrawler.tools.options.Config;

public record ExecutionParameters(String command, Config additionalConfig, String outputFormat) {

  public ExecutionParameters(String command, Config additionalConfig, String outputFormat) {
    this.command = requireNotBlank(command, "Command not provided");

    // Can be null
    this.additionalConfig = additionalConfig;

    final String outputFormatValue;
    if (isBlank(outputFormat)) {
      outputFormatValue = "text";
    } else {
      outputFormatValue = outputFormat.strip();
    }
    this.outputFormat = outputFormatValue;
  }

  public ExecutionParameters(String command, String outputFormat) {
    this(command, null, outputFormat);
  }
}
