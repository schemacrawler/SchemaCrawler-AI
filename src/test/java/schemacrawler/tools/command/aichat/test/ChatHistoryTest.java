package schemacrawler.tools.command.aichat.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.UserMessage;
import schemacrawler.tools.command.aichat.utility.ChatHistory;

public class ChatHistoryTest {

  @Test
  public void chatHistory() {
    final List<ChatMessage> messages = Arrays.asList(UserMessage.of("1"));
    final ChatHistory chatHistory = new ChatHistory(10, messages);
    chatHistory.add(UserMessage.of("2"));
    chatHistory.add(null);
    final List<ChatMessage> historyList = chatHistory.toList();
    assertThat(historyList, hasSize(2));
  }
}
