module us.fatehi.schemacrawler.ai_core {
  // Public API dependencies
  requires transitive us.fatehi.schemacrawler.schemacrawler;
  requires transitive us.fatehi.schemacrawler.scripting;
  requires transitive us.fatehi.schemacrawler.offline;
  requires transitive us.fatehi.schemacrawler.lint;

  // Implementation dependencies
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;

  // Export only the public API packages
  exports schemacrawler.tools.ai.tools;
  exports schemacrawler.tools.ai.utility;

  // Keep internal implementation packages encapsulated
  // - functions: not exported outside module
  // - model: not exported, but open to Jackson for reflection
  opens schemacrawler.tools.ai.model to com.fasterxml.jackson.databind;
}
