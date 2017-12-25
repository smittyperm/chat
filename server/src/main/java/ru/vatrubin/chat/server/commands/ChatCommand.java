package ru.vatrubin.chat.server.commands;

import ru.vatrubin.chat.server.ChatServer;
import ru.vatrubin.chat.server.ChatSession;

public abstract class ChatCommand {
    private String description;
    private ChatServer server;

    ChatCommand(String description, ChatServer server) {
        this.description = description;
        this.server = server;
    }

    String getDescription() {
        return description;
    }

    public abstract void handleCommand(ChatSession session, String commandParams);

    public ChatServer getServer() {
        return server;
    }
}
