package ru.vatrubin.chat.server.exceptions;

public class LoginInUseException extends LoginException {

    public LoginInUseException(String login) {
        super(login, "Login '" + login + "' is already in use. ");
    }
}
