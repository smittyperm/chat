package ru.vatrubin.chat.server.commands;

import ru.vatrubin.chat.server.ChatServer;
import ru.vatrubin.chat.server.ChatSession;

public class SetTopicCommand extends ChatCommand {
    public SetTopicCommand(String description, ChatServer server) {
        super(description, server);
    }

    @Override
    public void handleCommand(ChatSession session, String commandParams) {
        if (commandParams != null && commandParams.length() > 0) {
            getServer().setTopic(session, commandParams);
        } else {
            session.sendMessage("Can't set empty topic\r\n");
        }
    }
}
