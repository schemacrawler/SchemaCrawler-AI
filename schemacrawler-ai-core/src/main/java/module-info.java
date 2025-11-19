module us.fatehi.schemacrawler.ai_core {
  // Dependencies
  requires transitive us.fatehi.schemacrawler.schemacrawler;
  requires us.fatehi.schemacrawler.scripting;
  requires us.fatehi.schemacrawler.offline;
  requires us.fatehi.schemacrawler.lint;
  requires java.logging;
  requires java.sql;
  requires tools.jackson.core;
  requires transitive tools.jackson.databind;

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
