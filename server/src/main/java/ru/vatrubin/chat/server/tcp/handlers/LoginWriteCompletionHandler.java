package ru.vatrubin.chat.server.tcp.handlers;

import ru.vatrubin.chat.server.tcp.TcpServer;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class LoginWriteCompletionHandler implements CompletionHandler<Integer, Void> {

    private AsynchronousSocketChannel channel;
    private TcpServer server;

    LoginWriteCompletionHandler(TcpServer server, AsynchronousSocketChannel channel) {
        this.server = server;
        this.channel = channel;
    }

    public void completed(Integer result, Void attachment) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
        channel.read(inputBuffer, null, new LoginReadCompletionHandler(server, channel, inputBuffer));
    }

    public void failed(Throwable exc, Void attachment) {
    }

}
