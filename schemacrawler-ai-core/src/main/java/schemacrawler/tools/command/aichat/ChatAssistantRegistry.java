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

package schemacrawler.tools.command.aichat;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.command.aichat.options.AiChatCommandOptions;
import schemacrawler.tools.registry.BasePluginRegistry;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.StringFormat;

/** Chat assistant registry for loading chat assistant implementations. */
public final class ChatAssistantRegistry extends BasePluginRegistry {

  private static final Logger LOGGER = Logger.getLogger(ChatAssistantRegistry.class.getName());

  private static ChatAssistantRegistry chatAssistantRegistrySingleton;

  public static ChatAssistantRegistry getChatAssistantRegistry() {
    if (chatAssistantRegistrySingleton == null) {
      chatAssistantRegistrySingleton = new ChatAssistantRegistry();
    }
    chatAssistantRegistrySingleton.log();
    return chatAssistantRegistrySingleton;
  }

  private final List<Class<? extends ChatAssistant>> chatAssistantClasses;

  private ChatAssistantRegistry() {
    chatAssistantClasses = loadChatAssistantClasses();
  }

  @Override
  public String getName() {
    return "Chat Assistants";
  }

  @Override
  public Collection<PropertyName> getRegisteredPlugins() {
    final List<PropertyName> assistants = new ArrayList<>();
    for (final Class<? extends ChatAssistant> chatAssistantClass : chatAssistantClasses) {
      assistants.add(new PropertyName(chatAssistantClass.getSimpleName(), chatAssistantClass.getName()));
    }
    Collections.sort(assistants);
    return assistants;
  }

  public ChatAssistant newChatAssistant(
      final AiChatCommandOptions commandOptions,
      final Catalog catalog,
      final Connection connection) {

    if (chatAssistantClasses.isEmpty()) {
      throw new SchemaCrawlerException("No chat assistant implementation found");
    }

    for (final Class<? extends ChatAssistant> chatAssistantClass : chatAssistantClasses) {
      try {
        // Initialize the assistant with our parameters
        final java.lang.reflect.Constructor<?> constructor =
            chatAssistantClass.getConstructor(
                AiChatCommandOptions.class, Catalog.class, Connection.class);
        return (ChatAssistant) constructor.newInstance(commandOptions, catalog, connection);
      } catch (final Exception e) {
        LOGGER.log(
            Level.WARNING,
            String.format("Could not initialize chat assistant %s", chatAssistantClass.getName()),
            e);
      }
    }

    throw new SchemaCrawlerException("Could not initialize any chat assistant");
  }

  private List<Class<? extends ChatAssistant>> loadChatAssistantClasses() {
    final List<Class<? extends ChatAssistant>> chatAssistantClasses = new ArrayList<>();
    try {
      final ServiceLoader<ChatAssistant> serviceLoader =
          ServiceLoader.load(ChatAssistant.class, ChatAssistantRegistry.class.getClassLoader());
      for (final ChatAssistant chatAssistant : serviceLoader) {
        final Class<? extends ChatAssistant> chatAssistantClass = chatAssistant.getClass();
        LOGGER.log(
            Level.FINER,
            String.format("Loading chat assistant <%s>", chatAssistantClass.getName()));
        chatAssistantClasses.add(chatAssistantClass);
      }
    } catch (final Exception e) {
      throw new SchemaCrawlerException("Could not load chat assistant registry", e);
    }
    return chatAssistantClasses;
  }
}
