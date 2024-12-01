package schemacrawler.tools.command.chatgpt.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import io.github.sashirestela.openai.common.function.FunctionExecutor;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.AssistantMessage;
import schemacrawler.test.utility.TestOutputStream;
import schemacrawler.tools.command.chatgpt.utility.ChatGPTUtility;

public class ChatGPTUtilityTest {

  @Test
  public void printResponse() {
    final TestOutputStream stream = new TestOutputStream();
    final PrintStream out = new PrintStream(stream);

    assertThrows(NullPointerException.class, () -> ChatGPTUtility.printResponse(null, out));
    assertThrows(
        NullPointerException.class,
        () -> ChatGPTUtility.printResponse(Collections.emptyList(), null));

    final List<ChatMessage> completions =
        Arrays.asList(
            AssistantMessage.of("Well how are you?"),
            AssistantMessage.of("I am glad to hear that"));
    ChatGPTUtility.printResponse(completions, out);
    out.flush();
    assertThat(stream.getContents(), containsString("I am glad to hear that"));
  }

  @Test
  public void utility() throws Exception {
    final FunctionExecutor functionExecutor = ChatGPTUtility.newFunctionExecutor();
    assertThat(functionExecutor, is(not(nullValue())));
    assertThat(functionExecutor.getToolFunctions(), hasSize(6));
  }
}
