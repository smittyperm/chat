package ru.vatrubin.chat.client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChatClientConsole extends Thread {
    private Socket socket;
    private Scanner scanner;
    private PrintWriter socketWriter;

    ChatClientConsole(Socket socket) {
        this.socket = socket;
        scanner = new Scanner(System.in);
        try {
            socketWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),
                    StandardCharsets.UTF_8), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            String message = scanner.nextLine();
            if (!socket.isConnected() || socketWriter == null) {
                break;
            }
            if (message.length() > 1000) {
                System.out.println("Message too long. Max length 1000 characters.");
            } else {
                socketWriter.println(message);
            }
        }
    }
}
