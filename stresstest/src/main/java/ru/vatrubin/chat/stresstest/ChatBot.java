package ru.vatrubin.chat.stresstest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatBot extends Thread {

    private Socket socket;
    private BufferedReader socketIn;
    private String host;
    private Integer port;
    private int num;

    ChatBot(String host, Integer port, int num) {
        this.host = host;
        this.port = port;
        this.num = num;
    }

    @Override
    public void run() {
        connect();
    }

    private void connect() {
        try {
            socket = new Socket(host, port);
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("bot " + num + ": Can't connect to: " + host + ":" + port);
        }
        if (socket.isConnected()) {
            System.out.println("bot " + num + ":Connected to server: " + host + ":" + port);
            ChatMsgGenerator generator = new ChatMsgGenerator(socket, num);
            generator.start();
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
            }
            System.out.println("bot " + num + ":Disconnected from server.");
            generator.interrupt();
        }
    }
}
