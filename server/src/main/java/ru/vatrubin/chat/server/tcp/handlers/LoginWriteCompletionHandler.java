package ru.vatrubin.chat.server.tcp.handlers;

import ru.vatrubin.chat.server.tcp.TcpServer;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class LoginWriteCompletionHandler implements CompletionHandler<Integer, Void> {

    private AsynchronousSocketChannel channel;
    private TcpServer server;
    private ByteBuffer buffer;

    LoginWriteCompletionHandler(TcpServer server, AsynchronousSocketChannel channel, ByteBuffer buffer) {
        this.server = server;
        this.channel = channel;
        this.buffer = buffer;
    }

    public void completed(Integer result, Void attachment) {
        if (result < 0) {
            try {
                channel.close();
                return;
            } catch (Exception ignored) {
                return;
            }
        }
        if (buffer.hasRemaining()) {
            channel.write(buffer, null, this);
        } else {
            ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
            channel.read(inputBuffer, null, new LoginReadCompletionHandler(server, channel, inputBuffer));
        }
    }

    public void failed(Throwable exc, Void attachment) {
    }

}
