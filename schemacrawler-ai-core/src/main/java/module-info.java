module us.fatehi.schemacrawler.ai_core {

  // JDK dependencies
  requires java.logging;
  requires java.sql;

  // SchemaCrawler dependencies
  requires transitive us.fatehi.schemacrawler.schemacrawler;
  requires us.fatehi.schemacrawler.text;
  requires us.fatehi.schemacrawler.diagram;
  requires us.fatehi.schemacrawler.scripting;
  requires us.fatehi.schemacrawler.offline;
  requires us.fatehi.schemacrawler.lint;

  // Other dependencies
  requires tools.jackson.core;
  requires transitive tools.jackson.databind;

  // Service loader registrations
  uses schemacrawler.tools.ai.tools.FunctionDefinition;

  provides schemacrawler.tools.ai.tools.FunctionDefinition with
      schemacrawler.tools.ai.functions.DescribeTablesFunctionDefinition,
      schemacrawler.tools.ai.functions.DescribeRoutinesFunctionDefinition,
      schemacrawler.tools.ai.functions.LintFunctionDefinition,
      schemacrawler.tools.ai.functions.ListFunctionDefinition,
      schemacrawler.tools.ai.functions.ListAcrossTablesFunctionDefinition,
      schemacrawler.tools.ai.functions.DiagramFunctionDefinition,
      schemacrawler.tools.ai.functions.ServerInformationFunctionDefinition,
      schemacrawler.tools.ai.functions.TableSampleFunctionDefinition;

  // Export only the public API packages
  exports schemacrawler.tools.ai.tools;
  exports schemacrawler.tools.ai.utility;

  // Keep internal implementation packages encapsulated
  // not exported, but open to Jackson for reflection
  opens schemacrawler.tools.ai.model to
      tools.jackson.databind;
  opens schemacrawler.tools.ai.functions to
      tools.jackson.databind;
}
