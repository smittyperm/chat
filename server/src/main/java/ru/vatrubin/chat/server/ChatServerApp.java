package ru.vatrubin.chat.server;

import ru.vatrubin.chat.server.tcp.TcpServer;

public class ChatServerApp {
    public static void main(String[] args) {
        String port = args.length > 0 ? args[0] : "5555";
        String threads = args.length > 1 ? args[1] : "10";
        TcpServer server = new TcpServer(Integer.parseInt(port), Integer.parseInt(threads));
        server.run();

        while (!server.isStopped()) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                break;
            }
        }
        System.out.println("Server is stopped.");
    }
}

