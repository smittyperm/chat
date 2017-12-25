package ru.vatrubin.chat.server.tcp;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TcpUtils {
    public static String bytesBufferToString(Integer bytesRead, ByteBuffer inputBuffer) {
        byte[] buffer = new byte[bytesRead];
        inputBuffer.rewind();
        inputBuffer.get(buffer);
        String result = new String(buffer, StandardCharsets.UTF_8);
        inputBuffer.clear();
        return result;
    }

    public static ByteBuffer stringToBytesBuffer(String message) {
        byte[] doWrite = message.getBytes(StandardCharsets.UTF_8);
        return ByteBuffer.wrap(doWrite);
    }
}
