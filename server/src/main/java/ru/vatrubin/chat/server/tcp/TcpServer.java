package ru.vatrubin.chat.server.tcp;

import ru.vatrubin.chat.server.ChatServer;
import ru.vatrubin.chat.server.tcp.handlers.AcceptCompletionHandler;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer extends ChatServer {

    private int port;
    private int threadsNum;

    public TcpServer(int port, int threadsNum) {
        this.port = port;
        this.threadsNum = threadsNum;
    }

    @Override
    public void run() {
        try {
            ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(threadsNum);
            AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(newFixedThreadPool);

            final AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open(group);
            InetSocketAddress address = new InetSocketAddress(port);
            try {
                listener.bind(address);
                System.out.println("Tcp server working on port: " + port);
            } catch (BindException exception) {
                System.out.println("Server can't bind port: " + port);
                stopServer();
                return;
            }

            AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(listener, this);
            listener.accept(null, acceptCompletionHandler);
        } catch (Exception e) {
            e.printStackTrace();
            stopServer();
        }
    }
}
