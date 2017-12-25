package ru.vatrubin.chat.server;

import ru.vatrubin.chat.server.commands.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ChatServer {
    private final CopyOnWriteArrayList<ChatSession> sessions;
    private final ConcurrentSkipListSet<String> logins;
    private final SimpleDateFormat dateFormat;
    private final String msgFormat;
    private final String sysMsgFormat;
    private final CircularArrayList<String> history;
    private final Map<String, ChatCommand> chatCommandMap;

    private String topic = "";
    private Lock topicLock = new ReentrantLock();

    public ChatServer() {
        sessions = new CopyOnWriteArrayList<>();
        logins = new ConcurrentSkipListSet<>();
        dateFormat = new SimpleDateFormat("dd.MM HH:mm:ss");
        msgFormat = "%s %s: %s";
        sysMsgFormat = "%s: %s";
        history = new CircularArrayList<>(100);

        chatCommandMap = new LinkedHashMap<>();
        chatCommandMap.put("help", new HelpCommand("List of available commands", this));
        chatCommandMap.put("exit", new ExitCommand("Exit from chat", this));
        chatCommandMap.put("set_topic", new SetTopicCommand("Set topic of this chat", this));
        chatCommandMap.put("change_login", new ChangeLoginCommand("Change your login", this));
    }

    CopyOnWriteArrayList<ChatSession> getSessions() {
        return sessions;
    }

    public void registerSession(ChatSession session) {
        saveAndSendMessage(null, prepareSysMessage(session.getLogin() + " connected"));

        sessions.addIfAbsent(session);
        logins.add(session.getLogin());
        System.out.println("User connected (" + logins.size() + "): " + session.getLogin());

        for (String message : history.toArray(new String[history.size()])) {
            session.sendMessage(message);
        }


        topicLock.lock();
        session.sendMessage("Welcome to this chat, topic is " +
                (topic == null || topic.length() == 0
                ? "clear"
                :"\"" + topic + "\"") + "\r\n");
        topicLock.unlock();
    }

    public void setTopic(ChatSession session, String topic) {
        topicLock.lock();
        this.topic = topic;
        saveAndSendMessage(prepareSysMessage(session.getLogin() + " changed topic to \"" + topic + "\""));
        topicLock.unlock();
    }

    public void unRegisterSession(ChatSession session) {
        if (sessions.contains(session)) {
            sessions.remove(session);
            logins.remove(session.getLogin());
            System.out.println("User disconnected (" + logins.size() + "): " + session.getLogin());
            saveAndSendMessage(prepareSysMessage(session.getLogin() + " disconnected"));
        }
    }

    public void saveAndSendMessage(String message) {
        saveAndSendMessage(null, message);
    }

    private void saveAndSendMessage(ChatSession excludeSession, String message) {
        history.add(message);
        sendMessageToAll(excludeSession, message);
    }

    private void sendMessageToAll(ChatSession session, String message) {
        getSessions().forEach(sessionForSend -> {
            if (session == null || sessionForSend != session) {
                sessionForSend.sendMessage(message);
            }
        });
    }

    public void receiveMessage(ChatSession session, String message) {
        if (message.startsWith("/")) {
            message = message.substring(1, message.length()).trim();
            String commandName = message.split(" ")[0];
            ChatCommand chatCommand = chatCommandMap.get(commandName);
            if (chatCommand != null) {
                chatCommand.handleCommand(session, message.substring(commandName.length()).trim());
            } else {
                session.sendMessage("Unknown command: " + commandName + "\r\n");
            }
        } else {
            saveAndSendMessage(prepareMessage(session, message));
        }
    }

    private String prepareMessage(ChatSession session, String message) {
        return String.format(msgFormat, dateFormat.format(new Date()), session.getLogin(), message);
    }

    public String prepareSysMessage(String message) {
        return String.format(sysMsgFormat, dateFormat.format(new Date()), message + "\r\n");
    }

    public boolean loginInUse(String login) {
        return logins.contains(login);
    }

    public Map<String, ChatCommand> getChatCommandMap() {
        return chatCommandMap;
    }

    public boolean acceptableLogin(String login) {
        return login != null && login.matches("^[a-zA-Z0-9_-]{3,15}$");
    }

    public ConcurrentSkipListSet<String> getLogins() {
        return logins;
    }
}
