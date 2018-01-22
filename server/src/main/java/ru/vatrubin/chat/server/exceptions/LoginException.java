package ru.vatrubin.chat.server.exceptions;

public abstract class LoginException extends RuntimeException {
    private String login;

    public LoginException(String login, String message) {
        super(message);
        this.login = login;
    }
}
