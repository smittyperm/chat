package ru.vatrubin.chat.server.tcp.handlers;

import ru.vatrubin.chat.server.tcp.TcpServer;
import ru.vatrubin.chat.server.tcp.TcpUtils;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {

    private AsynchronousServerSocketChannel listener;
    private TcpServer server;

    public AcceptCompletionHandler(AsynchronousServerSocketChannel listener, TcpServer server) {
        this.listener = listener;
        this.server = server;
    }

    public void completed(AsynchronousSocketChannel channel, Void arg1) {
        System.out.println("Client connected: " + channel);

        listener.accept(null, this);

        ByteBuffer outputBuffer = TcpUtils.stringToBytesBuffer("This is chat, please choose login: " +
                TcpUtils.getEndOfLine());
        channel.write(outputBuffer, null, new LoginWriteCompletionHandler(server, channel, outputBuffer));
    }

    public void failed(Throwable arg0, Void arg1) {
    }
}