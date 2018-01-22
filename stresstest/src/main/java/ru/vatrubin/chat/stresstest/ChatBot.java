package ru.vatrubin.chat.stresstest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class ChatBot extends Thread {

    private Socket socket;
    private BufferedReader socketIn;
    private String host;
    private Integer port;
    private int num;
    private String login;
    private ConcurrentHashMap<String, Date> msgMap;

    ChatBot(String host, Integer port, int num) {
        this.host = host;
        this.port = port;
        this.num = num;
        msgMap = new ConcurrentHashMap<>();
        login = "testBot" + num;
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
            System.out.println("bot " + num + ": Connected to server: " + host + ":" + port);

            ChatMsgGenerator generator = new ChatMsgGenerator(socket, login, msgMap);
            generator.start();
            while (true) {
                String message;
                try {
                    message = socketIn.readLine();
                    if (message.contains(login)) {
                        String[] msgParts = message.split(login + ": ");
                        if (msgParts.length > 1) {
                            String msgContent = message.split(login + ": ")[1].trim();
                            if (msgMap.containsKey(msgContent)) {
                                Date msgDate = msgMap.remove(msgContent);
                                Long time = new Date().getTime() - msgDate.getTime();
                                System.out.println(login + " get message after " + (time/1000d) + " s");
                            }
                        }
                    }
                } catch (IOException e) {
                    break;
                }
            }
            System.out.println("bot " + num + ":Disconnected from server.");
            generator.interrupt();
        }
    }
}
