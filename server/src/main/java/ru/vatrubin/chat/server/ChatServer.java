package ru.vatrubin.chat.server;

import ru.vatrubin.chat.server.commands.ChangeLoginCommand;
import ru.vatrubin.chat.server.commands.ChatCommand;
import ru.vatrubin.chat.server.commands.ExitCommand;
import ru.vatrubin.chat.server.commands.HelpCommand;
import ru.vatrubin.chat.server.commands.SetTopicCommand;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class ChatServer {
    private final CopyOnWriteArrayList<ChatSession> sessions;
    private final Set<String> logins;
    private final ReadWriteLock loginsLock = new ReentrantReadWriteLock();
    private final DateTimeFormatter dateTimeFormatter;
    private final String msgFormat;
    private final String sysMsgFormat;
    private final CircularHistory history;
    private final Map<String, ChatCommand> chatCommandMap;

    private String topic = "";
    private ReadWriteLock topicLock = new ReentrantReadWriteLock();

    public ChatServer() {
        sessions = new CopyOnWriteArrayList<>();
        logins = new HashSet<>();
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm:ss");
        msgFormat = "%s %s: %s";
        sysMsgFormat = "%s: %s";
        history = new CircularHistory(100);

        chatCommandMap = new LinkedHashMap<>();
        chatCommandMap.put("help", new HelpCommand(this));
        chatCommandMap.put("exit", new ExitCommand(this));
        chatCommandMap.put("set_topic", new SetTopicCommand(this));
        chatCommandMap.put("change_login", new ChangeLoginCommand(this));
    }

    public void registerSession(ChatSession session) {
        saveAndSendMessage(null, prepareSysMessage(session.getLogin() + " connected"));

        sessions.addIfAbsent(session);
        loginsLock.writeLock().lock();
        logins.add(session.getLogin());
        System.out.println("User connected (" + logins.size() + "): " + session.getLogin());
        loginsLock.writeLock().unlock();

        for (String message : history.getArray()) {
            session.sendMessage(message);
        }


        topicLock.readLock().lock();
        session.sendMessage("Welcome to this chat, topic is " +
                (topic == null || topic.length() == 0
                        ? "clear"
                        : "\"" + topic + "\"") + "\r\n");
        topicLock.readLock().unlock();
    }

    public void setTopic(ChatSession session, String topic) {
        topicLock.writeLock().lock();
        this.topic = topic;
        saveAndSendMessage(prepareSysMessage(session.getLogin() + " changed topic to \"" + topic + "\""));
        topicLock.writeLock().unlock();
    }

    public void unRegisterSession(ChatSession session) {
        if (sessions.remove(session)) {
            loginsLock.writeLock().lock();
            logins.remove(session.getLogin());
            System.out.println("User disconnected (" + logins.size() + "): " + session.getLogin());
            loginsLock.writeLock().unlock();
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
        sessions.forEach(sessionForSend -> {
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
        return String.format(msgFormat, LocalDateTime.now().format(dateTimeFormatter), session.getLogin(), message);
    }

    public String prepareSysMessage(String message) {
        return String.format(sysMsgFormat, LocalDateTime.now().format(dateTimeFormatter), message + "\r\n");
    }

    public Map<String, ChatCommand> getChatCommandMap() {
        return Collections.unmodifiableMap(chatCommandMap);
    }

    public boolean acceptableLogin(String login) {
        return login != null && login.matches("^[a-zA-Z0-9_-]{3,15}$");
    }

    public boolean containsLogin(String login) {
        loginsLock.readLock().lock();
        boolean result = logins.contains(login);
        loginsLock.readLock().unlock();
        return result;
    }

    public void replaceLogin(String oldLogin, String newLogin) {
        loginsLock.writeLock().lock();
        logins.remove(oldLogin);
        logins.add(newLogin);
        loginsLock.writeLock().unlock();
    }

    boolean containsSession(ChatSession session) {
        return sessions.contains(session);
    }

    String[] getCircularHistory() {
        return history.getArray();
    }
}
