package ru.vatrubin.chat.server.exceptions;

public class LoginIsNotAcceptableException extends LoginException {

    public LoginIsNotAcceptableException(String login) {
        super(login, "Login '" + login + "' is not acceptable. " +
                "Login can contain only letters, numbers and _ symbol, with length 3-15. ");
    }
}
