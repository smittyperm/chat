package ru.vatrubin.chat.server.commands;

import ru.vatrubin.chat.server.ChatServer;
import ru.vatrubin.chat.server.ChatSession;

public class HelpCommand extends ChatCommand {

    public HelpCommand(String description, ChatServer server) {
        super(description, server);
    }

    @Override
    public void handleCommand(ChatSession session, String commandParams) {
        StringBuilder result = new StringBuilder();
        getServer().getChatCommandMap().forEach((name, command) ->
                result.append("/").append(name).append(" - ").append(command.getDescription()).append("\r\n"));
        session.sendMessage(result.toString());
    }
}
