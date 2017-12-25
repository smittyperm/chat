package ru.vatrubin.chat.server.commands;

import ru.vatrubin.chat.server.ChatServer;
import ru.vatrubin.chat.server.ChatSession;

public class SetTopicCommand extends ChatCommand {
    public SetTopicCommand(ChatServer server) {
        super("Set topic of this chat", server);
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
