package ru.vatrubin.chat.server.tcp;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TcpUtils {
    private final static String END_OF_LINE = "\r\n";

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

    public static boolean stringContainsEndOfLine(String s) {
        return s.contains(END_OF_LINE);
    }

    public static boolean stringEndsWithEndOfLine(String s) {
        return s.endsWith(  END_OF_LINE);
    }

    public static String[] splitMessages(String s) {
        return s.split(END_OF_LINE);
    }

    public static String getEndOfLine() {
        return END_OF_LINE;
    }
}
