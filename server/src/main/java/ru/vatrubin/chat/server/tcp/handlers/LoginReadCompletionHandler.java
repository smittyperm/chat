package ru.vatrubin.chat.server.tcp.handlers;

import ru.vatrubin.chat.server.tcp.TcpChatSession;
import ru.vatrubin.chat.server.tcp.TcpServer;
import ru.vatrubin.chat.server.tcp.TcpUtils;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class LoginReadCompletionHandler implements CompletionHandler<Integer, Void> {
    private AsynchronousSocketChannel channel;
    private TcpServer server;
    private ByteBuffer inputBuffer;

    LoginReadCompletionHandler(TcpServer server, AsynchronousSocketChannel channel, ByteBuffer inputBuffer) {
        this.server = server;
        this.channel = channel;
        this.inputBuffer = inputBuffer;
    }

    @Override
    public void completed(Integer bytesRead, Void attachment) {
        if (bytesRead < 1) {
            System.out.println("Closing connection to " + channel);
        } else {
            String login = TcpUtils.bytesBufferToString(bytesRead, inputBuffer).trim();
            if (server.containsLogin(login)) {
                ByteBuffer outputBuffer =
                        TcpUtils.stringToBytesBuffer("This login is already in use, please choose another one: \r\n");
                channel.write(outputBuffer, null, new LoginWriteCompletionHandler(server, channel));
            } else if (!server.acceptableLogin(login)) {
                ByteBuffer outputBuffer =
                        TcpUtils.stringToBytesBuffer("Login can contain only letters, numbers and _ symbol, " +
                                "with length 3-15. " +
                                "Please choose another one: \r\n");
                channel.write(outputBuffer, null, new LoginWriteCompletionHandler(server, channel));
            } else {
                TcpChatSession session = new TcpChatSession(server, channel, login);
                server.registerSession(session);

                ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
                channel.read(inputBuffer, session, new ReadCompletionHandler(inputBuffer, server));
            }
        }
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        System.out.println("Closing connection to " + channel);
    }
}
