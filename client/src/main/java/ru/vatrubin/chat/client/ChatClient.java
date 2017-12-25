package ru.vatrubin.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

class ChatClient {
    private Socket socket;
    private BufferedReader socketIn;
    private String host;
    private Integer port;

    ChatClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    void connect() {
        try {
            socket = new Socket(host, port);
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Can't connect to: " + host + ":" + port);
        }
        if (socket.isConnected()) {
            System.out.println("Connected to server: " + host + ":" + port);
            ChatClientConsole scanner = new ChatClientConsole(socket);
            scanner.start();
            while (true) {
                String message;
                try {
                    message = socketIn.readLine();
                } catch (IOException e) {
                    break;
                }
                if (message == null) {
                    break;
                }
                System.out.println(message);
            }
            System.out.println("Disconnected from server.");
            System.exit(0);
        }
    }
}
