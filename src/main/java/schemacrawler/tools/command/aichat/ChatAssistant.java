package schemacrawler.tools.command.aichat;

public interface ChatAssistant extends AutoCloseable {

  String chat(String prompt);

  boolean shouldExit();
}
