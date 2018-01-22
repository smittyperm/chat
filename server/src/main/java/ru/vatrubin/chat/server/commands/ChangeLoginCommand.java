package ru.vatrubin.chat.server.commands;

import ru.vatrubin.chat.server.ChatServer;
import ru.vatrubin.chat.server.ChatSession;
import ru.vatrubin.chat.server.exceptions.LoginException;

public class ChangeLoginCommand extends ChatCommand {
    public ChangeLoginCommand(ChatServer server) {
        super("Change your login", server);
    }

    @Override
    public void handleCommand(ChatSession session, String commandParams) {
        try {
            String oldLogin = session.getLogin();
            getServer().changeLogin(session, commandParams);
            getServer().saveAndSendMessage(
                    getServer().prepareSysMessage("User " + oldLogin + " change login to " + commandParams));
        } catch (LoginException e) {
            session.sendMessage(e.getMessage() + "Please choose another one.");
        }
    }
}
