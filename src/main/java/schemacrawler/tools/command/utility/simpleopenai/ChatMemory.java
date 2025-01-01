/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.utility.simpleopenai;

import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.requireNonNull;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import us.fatehi.utility.collections.CircularBoundedList;

public class ChatMemory {

  private final CircularBoundedList<ChatMessage> chatMemory;
  private final List<ChatMessage> systemMessages;

  public ChatMemory(final int context, final List<ChatMessage> systemMessages) {
    chatMemory = new CircularBoundedList<>(context);
    this.systemMessages = requireNonNull(systemMessages, "No system messages provided");
  }

  public void add(final ChatMessage message) {
    if (message != null) {
      chatMemory.add(message);
    }
  }

  public List<ChatMessage> toList() {
    final List<ChatMessage> chatMessages = new ArrayList<>(chatMemory.convertToList());
    for (final ChatMessage systemMessage : systemMessages) {
      chatMessages.add(0, systemMessage);
    }
    return chatMessages;
  }
}
