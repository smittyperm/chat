package ru.vatrubin.chat.server.tcp.handlers;


import ru.vatrubin.chat.server.ChatServer;
import ru.vatrubin.chat.server.tcp.TcpChatSession;

import java.nio.channels.CompletionHandler;

public class WriteCompletionHandler implements CompletionHandler<Integer, TcpChatSession> {
    private ChatServer server;

    public WriteCompletionHandler(ChatServer server) {
        this.server = server;
    }

    public void completed(Integer result, TcpChatSession session) {
        session.checkMsgQueue();
    }

    public void failed(Throwable exc, TcpChatSession session) {
        server.unRegisterSession(session);
    }

}
