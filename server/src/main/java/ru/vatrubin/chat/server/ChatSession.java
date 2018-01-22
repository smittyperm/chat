package ru.vatrubin.chat.server;

public abstract class ChatSession {
    protected ChatServer server;
    private String login;

    public ChatSession(ChatServer server, String login) {
        this.server = server;
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public abstract void sendMessage(String message);

    public void disconnect() {
        server.unRegisterSession(this);
    }
}
