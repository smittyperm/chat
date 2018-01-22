package ru.vatrubin.chat.server.tcp.handlers;

import ru.vatrubin.chat.server.exceptions.LoginException;
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
    private StringBuilder messageBuilder;

    LoginReadCompletionHandler(TcpServer server, AsynchronousSocketChannel channel, ByteBuffer inputBuffer) {
        this.server = server;
        this.channel = channel;
        this.inputBuffer = inputBuffer;
        messageBuilder = new StringBuilder();
    }

    @Override
    public void completed(Integer bytesRead, Void attachment) {
        if (bytesRead < 1) {
            System.out.println("Closing connection to " + channel);
            try {
                channel.close();
            } catch (Exception ignored) {
            }
        } else {
            messageBuilder.append(TcpUtils.bytesBufferToString(bytesRead, inputBuffer));
            if (!TcpUtils.stringContainsEndOfLine(messageBuilder.toString())) {
                inputBuffer = ByteBuffer.allocate(1024);
                channel.read(inputBuffer, null, this);
                return;
            }
            String login = TcpUtils.splitMessages(messageBuilder.toString())[0].trim();
            TcpChatSession session = new TcpChatSession(server, channel, login);
            try {
                server.registerSession(session);
            } catch (LoginException e) {
                ByteBuffer outputBuffer =
                        TcpUtils.stringToBytesBuffer(e.getMessage() +
                                "Please choose another one: " + TcpUtils.getEndOfLine());
                channel.write(outputBuffer, null,
                        new LoginWriteCompletionHandler(server, channel, outputBuffer));
                return;
            }

            ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
            channel.read(inputBuffer, session, new ReadCompletionHandler(inputBuffer, server));
        }
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        System.out.println("Closing connection to " + channel);
    }
}
