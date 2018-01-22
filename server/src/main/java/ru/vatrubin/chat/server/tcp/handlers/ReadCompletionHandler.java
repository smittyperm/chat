package ru.vatrubin.chat.server.tcp.handlers;

import ru.vatrubin.chat.server.tcp.TcpChatSession;
import ru.vatrubin.chat.server.tcp.TcpServer;
import ru.vatrubin.chat.server.tcp.TcpUtils;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

public class ReadCompletionHandler implements CompletionHandler<Integer, TcpChatSession> {

    private ByteBuffer inputBuffer;
    private TcpServer server;
    private StringBuilder messageBuilder;

    ReadCompletionHandler(ByteBuffer inputBuffer, TcpServer server) {
        this.inputBuffer = inputBuffer;
        this.server = server;
        messageBuilder = new StringBuilder();
    }

    public void completed(Integer bytesRead, TcpChatSession session) {
        if (bytesRead < 1) {
            System.out.println("Closing connection to " + session.getChannel());
            session.disconnect();
        } else {
            messageBuilder.append(TcpUtils.bytesBufferToString(bytesRead, inputBuffer));
            if (!TcpUtils.stringContainsEndOfLine(messageBuilder.toString())) {
                inputBuffer = ByteBuffer.allocate(1024);
                session.getChannel().read(inputBuffer, session, this);
                return;
            } else {
                String[] messages = TcpUtils.splitMessages(messageBuilder.toString());
                boolean fullTail = TcpUtils.stringEndsWithEndOfLine(messageBuilder.toString());
                if (fullTail || messages.length > 1) {
                    for (int i = 0; i < (fullTail ? messages.length : messages.length - 1); i++) {
                        String message = messages[i];
                        System.out.println("Message from " + session.getLogin() + ": " + message);
                        server.receiveMessage(session, message);
                    }
                    messageBuilder = fullTail
                            ? new StringBuilder()
                            : new StringBuilder(messages[messages.length - 1]);
                }
            }
            session.getChannel().read(inputBuffer, session, this);
        }
    }

    public void failed(Throwable exc, TcpChatSession session) {
        System.out.println("Closing connection to " + session.getChannel());
        session.disconnect();
    }
}
