package ru.vatrubin.chat.server;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CircularHistoryTest {

    private CircularHistory circularHistory;
    private int circleSize = 20;

    @Before
    public void prepare() {
        circularHistory = new CircularHistory(circleSize);
    }

    @Test
    public void testCircle() {
        assertEquals(0, circularHistory.getArray().length);

        int start = 1;
        int end = 5;
        for (int i = start; i <= end; i++) {
            circularHistory.add(String.valueOf(i));
        }

        String[] result = circularHistory.getArray();
        assertEquals(end, result.length);
        assertEquals(String.valueOf(start), result[0]);
        assertEquals(String.valueOf(end), result[result.length - 1]);

        circularHistory.clear();
        result = circularHistory.getArray();
        assertEquals(0, result.length);

        start = 1;
        end = 50;
        for (int i = start; i <= end; i++) {
            circularHistory.add(String.valueOf(i));
        }
        result = circularHistory.getArray();
        assertEquals(circleSize, result.length);
        assertEquals(String.valueOf(end - circleSize + 1), result[0]);
        assertEquals(String.valueOf(end), result[result.length - 1]);
    }
}