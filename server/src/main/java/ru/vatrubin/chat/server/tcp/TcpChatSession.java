package ru.vatrubin.chat.server.tcp;

import ru.vatrubin.chat.server.ChatSession;
import ru.vatrubin.chat.server.tcp.handlers.WriteCompletionHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TcpChatSession extends ChatSession {
    private AsynchronousSocketChannel channel;
    private LinkedBlockingQueue<String> msgQueue;
    private Lock writeLock;
    private boolean isPending;

    public TcpChatSession(TcpServer server, AsynchronousSocketChannel channel, String userName) {
        super(server, userName);
        this.channel = channel;
        msgQueue = new LinkedBlockingQueue<>();
        writeLock = new ReentrantLock();
    }

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    @Override
    public void sendMessage(String message) {
        message = message + TcpUtils.getEndOfLine();
        writeLock.lock();
        try {
            if (msgQueue.isEmpty() && !isPending) {
                writeMsgToChannel(message);
            } else {
                msgQueue.add(message);
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void checkMsgQueue() {
        writeLock.lock();
        try {
            if (!msgQueue.isEmpty()) {
                writeMsgToChannel(msgQueue.poll());
            } else {
                isPending = false;
            }
        } finally {
            writeLock.unlock();
        }
    }

    private void writeMsgToChannel(String message) {
        isPending = true;
        ByteBuffer buffer = TcpUtils.stringToBytesBuffer(message);
        channel.write(buffer,this, new WriteCompletionHandler(server, buffer));
    }

    @Override
    public void disconnect() {
        try {
            channel.close();
        } catch (IOException ignored){
        } finally {
            super.disconnect();
        }
    }
}
