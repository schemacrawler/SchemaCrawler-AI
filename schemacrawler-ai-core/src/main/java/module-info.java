module us.fatehi.schemacrawler.ai_core {
  // Dependencies
  requires us.fatehi.schemacrawler.schemacrawler;
  requires us.fatehi.schemacrawler.scripting;
  requires us.fatehi.schemacrawler.offline;
  requires us.fatehi.schemacrawler.lint;
  requires java.logging;
  requires java.sql;
  requires com.fasterxml.jackson.module.jsonSchema;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;

  // Export only the public API packages
  exports schemacrawler.tools.ai.tools;
  exports schemacrawler.tools.ai.utility;

  // Keep internal implementation packages encapsulated
  // not exported, but open to Jackson for reflection
  opens schemacrawler.tools.ai.model to
      com.fasterxml.jackson.databind;
  opens schemacrawler.tools.ai.functions to
      com.fasterxml.jackson.databind;
}
