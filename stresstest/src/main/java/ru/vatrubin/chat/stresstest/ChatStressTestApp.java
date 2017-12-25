package ru.vatrubin.chat.stresstest;

public class ChatStressTestApp {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        String port = args.length > 1 ? args[1] : "5555";
        String connections = args.length > 2 ? args[2] : "1000";
        for (int i = 1; i <= Integer.parseInt(connections); i++) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new ChatBot(host, Integer.parseInt(port), i).start();
        }
    }
}
