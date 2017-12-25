package ru.vatrubin.chat.server;

import ru.vatrubin.chat.server.tcp.TcpServer;

public class ChatServerApp {
    public static void main(String[] args) {
        String port = args.length > 0 ? args[0] : "5555";
        String threads = args.length > 1 ? args[1] : "10";
        try {
            TcpServer server = new TcpServer();
            server.run(Integer.parseInt(port), Integer.parseInt(threads));
            try {
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

