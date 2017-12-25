package ru.vatrubin.chat.stresstest;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ChatMsgGenerator extends Thread {
    private Socket socket;
    private PrintWriter socketWriter;
    private int num;

    ChatMsgGenerator(Socket socket, int num) {
        this.socket = socket;
        this.num = num;
        try {
            socketWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),
                    StandardCharsets.UTF_8), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String message = "tester" + num;
        socketWriter.println(message);
        while (true) {
            message = UUID.randomUUID().toString();
            if (!socket.isConnected() || socketWriter == null) {
                break;
            }
            socketWriter.println(message);
            try {
                sleep(ThreadLocalRandom.current().nextInt(5, 60 + 1) * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

