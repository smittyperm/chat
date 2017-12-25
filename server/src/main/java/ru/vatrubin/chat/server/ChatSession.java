package ru.vatrubin.chat.server;

public abstract class ChatSession {
    private String login;

    public String getLogin() {
        return login;
    }

    public ChatSession(String login) {
        this.login = login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public abstract void sendMessage(String message);

    public abstract void disconnect();
}
