package ru.vatrubin.chat.server.commands;

import ru.vatrubin.chat.server.ChatServer;
import ru.vatrubin.chat.server.ChatSession;

public class ChangeLoginCommand extends ChatCommand {
    public ChangeLoginCommand(String description, ChatServer server) {
        super(description, server);
    }

    @Override
    public void handleCommand(ChatSession session, String commandParams) {
        if (getServer().loginInUse(commandParams)) {
            session.sendMessage("This login is already in use, please choose another one: \r\n");
        } else if (!getServer().acceptableLogin(commandParams)) {
            session.sendMessage("Login can contain only letters, numbers and _ symbol, with length 3-15. " +
                    "Please choose another one: \r\n");
        } else {
            getServer().getLogins().remove(session.getLogin());
            getServer().saveAndSendMessage(
                    getServer().prepareSysMessage("User " + session.getLogin() + " change login to " + commandParams));
            session.setLogin(commandParams);
            getServer().getLogins().add(session.getLogin());
        }
    }
}
