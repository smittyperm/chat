package ru.vatrubin.chat.server;

import org.junit.Before;
import org.junit.Test;
import ru.vatrubin.chat.server.exceptions.LoginInUseException;
import ru.vatrubin.chat.server.exceptions.LoginIsNotAcceptableException;
import ru.vatrubin.chat.server.tcp.TcpServer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ChatServerTest {

    private ChatServer server;

    private SimpleChatSession spectatorSession;

    @Before
    public void setUp() throws Exception {
        server = new ChatServer() {
            @Override
            public void run() {
            }
        };
        spectatorSession = new SimpleChatSession(server, "spt");
        server.registerSession(spectatorSession);
    }

    @Test
    public void testRegistration() {
        spectatorSession.clearHist();
        ChatSession session = new SimpleChatSession(server, "user1");
        server.registerSession(session);
        assertTrue(server.containsLogin("user1"));
        assertTrue(server.containsSession(session));
        assertNotNull(spectatorSession.getLastMsg());

        try {
            server.registerSession(new SimpleChatSession(server, "user1"));
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof LoginInUseException);
        }

        try {
            server.registerSession(new SimpleChatSession(server, "a"));
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof LoginIsNotAcceptableException);
        }

        assertTrue(server.containsLogin("user1"));

        server.receiveMessage(session, "/change_login spt");
        assertEquals("user1", session.getLogin());
        server.receiveMessage(session, "/change_login a");
        assertEquals("user1", session.getLogin());

        String newUsername = "awesome_user";
        server.receiveMessage(session, "/change_login " + newUsername);
        assertTrue(spectatorSession.getLastMsg().contains(newUsername));
        assertFalse(server.containsLogin("user1"));
        assertTrue(server.containsLogin(newUsername));

        assertFalse(server.containsLogin("user1"));

        spectatorSession.clearHist();
        session.disconnect();
        assertFalse(server.containsLogin(newUsername));
        assertFalse(server.containsSession(session));
        assertNotNull(spectatorSession.getLastMsg());
    }

    @Test
    public void testLogin() {
        assertFalse(server.acceptableLogin("1"));
        assertFalse(server.acceptableLogin("#123"));
        assertFalse(server.acceptableLogin("1234567890123456"));

        assertTrue(server.acceptableLogin("uSeRnAmE1234567"));
    }

    @Test
    public void testMsg() {
        SimpleChatSession session = new SimpleChatSession(server, "user2");
        server.registerSession(session);
        String message1 = "msg1";
        session.sendMessage(message1);
        assertEquals(message1, session.getLastMsg());

        String message2 = "msg2";
        session.clearHist();
        spectatorSession.clearHist();
        server.receiveMessage(session, message2);
        assertEquals(spectatorSession.getLastMsg(), session.getLastMsg());
    }

    @Test
    public void testContainsHelpCommand() {
        assertTrue(server.getChatCommandMap().containsKey("help"));
    }

    @Test
    public void testTopic() {
        SimpleChatSession session = new SimpleChatSession(server, "user3");
        server.registerSession(session);

        String topic = "awesome_topic";
        server.receiveMessage(session, "/set_topic " + topic);

        SimpleChatSession session1 = new SimpleChatSession(server, "user4");
        server.registerSession(session1);

        server.receiveMessage(session, "/set_topic ");

        assertTrue(session1.getLastMsg().contains(topic));
    }

    @Test
    public void testExit() {
        SimpleChatSession session = new SimpleChatSession(server, "user5");
        server.registerSession(session);
        server.receiveMessage(session, "/exit");

        server.receiveMessage(session, "/unknown_command");
        assertTrue(session.getLastMsg().toLowerCase().contains("unknown"));

        assertTrue(session.disconnected);
    }

    @Test
    public void testHelp() {
        SimpleChatSession session = new SimpleChatSession(server, "user6");
        server.registerSession(session);
        server.receiveMessage(session, "/help");

        assertTrue(session.getLastMsg().contains("List of available commands"));
    }

    class SimpleChatSession extends ChatSession {
        boolean disconnected;
        List<String> hist = new ArrayList<>();

        public SimpleChatSession(ChatServer server, String login) {
            super(server, login);
        }

        @Override
        public void disconnect() {
            disconnected = true;
            super.disconnect();
        }

        @Override
        public void sendMessage(String message) {
            hist.add(message);
        }

        void clearHist() {
            hist.clear();
        }

        String getLastMsg() {
            return hist.get(hist.size() - 1);
        }

        List<String> getHist() {
            return hist;
        }
    }
}