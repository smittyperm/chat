package ru.vatrubin.chat.server.tcp.handlers;

import ru.vatrubin.chat.server.tcp.TcpChatSession;
import ru.vatrubin.chat.server.tcp.TcpServer;
import ru.vatrubin.chat.server.tcp.TcpUtils;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

public class ReadCompletionHandler implements CompletionHandler<Integer, TcpChatSession> {

    private ByteBuffer inputBuffer;
    private TcpServer server;

    ReadCompletionHandler(ByteBuffer inputBuffer, TcpServer server) {
        this.inputBuffer = inputBuffer;
        this.server = server;
    }

    public void completed(Integer bytesRead, TcpChatSession session) {
        if (bytesRead < 1) {
            System.out.println("Closing connection to " + session.getChannel());
            server.unRegisterSession(session);
        } else {
            String message = TcpUtils.bytesBufferToString(bytesRead, inputBuffer);
            System.out.print("Message from " + session.getLogin() + ": " + message);
            session.getChannel().read(inputBuffer, session, this);
            server.receiveMessage(session, message);
        }
    }

    public void failed(Throwable exc, TcpChatSession session) {
        System.out.println("Closing connection to " + session.getChannel());
        server.unRegisterSession(session);
    }
}
