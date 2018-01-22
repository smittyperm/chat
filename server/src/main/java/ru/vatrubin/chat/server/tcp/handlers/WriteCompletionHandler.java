package ru.vatrubin.chat.server.tcp.handlers;


import ru.vatrubin.chat.server.ChatServer;
import ru.vatrubin.chat.server.tcp.TcpChatSession;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

public class WriteCompletionHandler implements CompletionHandler<Integer, TcpChatSession> {
    private ChatServer server;
    private ByteBuffer buffer;

    public WriteCompletionHandler(ChatServer server, ByteBuffer buffer) {
        this.server = server;
        this.buffer = buffer;
    }

    public void completed(Integer result, TcpChatSession session) {
        if (result < 0) {
            try {
                session.disconnect();
            } catch (Exception ignored) {
                return;
            }
        }
        if (buffer.hasRemaining()) {
            session.getChannel().write(buffer, session, this);
        } else {
            session.checkMsgQueue();
        }
    }

    public void failed(Throwable exc, TcpChatSession session) {
        session.disconnect();
    }

}
