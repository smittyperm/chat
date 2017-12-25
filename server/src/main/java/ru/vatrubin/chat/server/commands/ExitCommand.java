package ru.vatrubin.chat.server.commands;

import ru.vatrubin.chat.server.ChatServer;
import ru.vatrubin.chat.server.ChatSession;

public class ExitCommand extends ChatCommand {
    public ExitCommand(ChatServer server) {
        super("Exit from chat", server);
    }

    @Override
    public void handleCommand(ChatSession session, String commandParams) {
        session.disconnect();
    }
}
