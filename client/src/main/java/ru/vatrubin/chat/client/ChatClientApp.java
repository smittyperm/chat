package ru.vatrubin.chat.client;

public class ChatClientApp {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        String port = args.length > 1 ? args[1] : "5555";
        new ChatClient(host, Integer.parseInt(port)).connect();
    }
}

