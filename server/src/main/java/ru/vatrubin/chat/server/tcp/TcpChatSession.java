package ru.vatrubin.chat.server.tcp;

import ru.vatrubin.chat.server.ChatSession;
import ru.vatrubin.chat.server.tcp.handlers.WriteCompletionHandler;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TcpChatSession extends ChatSession {
    private AsynchronousSocketChannel channel;
    private TcpServer server;
    private LinkedBlockingQueue<String> msgQueue;
    private ReentrantReadWriteLock pendingLock;
    private boolean isPending;

    public TcpChatSession(TcpServer server, AsynchronousSocketChannel channel, String userName) {
        super(userName);
        this.channel = channel;
        this.server = server;
        msgQueue = new LinkedBlockingQueue<>();
        pendingLock = new ReentrantReadWriteLock();
    }

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    @Override
    public void sendMessage(String message) {
        pendingLock.readLock().lock();
        if (msgQueue.isEmpty() && !isPending) {
            writeMsgToChannel(message);
        } else {
            msgQueue.add(message);
        }
        pendingLock.readLock().unlock();
    }

    public void checkMsgQueue() {
        pendingLock.writeLock().lock();
        if (!msgQueue.isEmpty()) {
            writeMsgToChannel(msgQueue.poll());
        } else {
            isPending = false;
        }
        pendingLock.writeLock().unlock();
    }

    private void writeMsgToChannel(String message) {
        isPending = true;
        channel.write(TcpUtils.stringToBytesBuffer(message),
                this, new WriteCompletionHandler(server));
    }

    @Override
    public void disconnect() {
        try {
            channel.close();
        } catch (IOException ignored){
        } finally {
            server.unRegisterSession(this);
        }
    }
}
