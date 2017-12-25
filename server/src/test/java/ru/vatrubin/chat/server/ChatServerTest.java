package ru.vatrubin.chat.server;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ChatServerTest {

    private ChatServer server;

    private SimpleChatSession spectatorSession = new SimpleChatSession("spt");

    @Before
    public void setUp() throws Exception {
        server = new ChatServer() {
        };
        server.registerSession(spectatorSession);
    }

    @Test
    public void testRegistration() {
        spectatorSession.clearHist();
        ChatSession session = new SimpleChatSession("user1");
        server.registerSession(session);
        assertTrue(server.getLogins().contains("user1"));
        assertTrue(server.getSessions().contains(session));
        assertNotNull(spectatorSession.getLastMsg());

        assertTrue(server.loginInUse("user1"));

        server.receiveMessage(session, "/change_login spt");
        assertEquals(session.getLogin(), "user1");
        server.receiveMessage(session, "/change_login a");
        assertEquals(session.getLogin(), "user1");

        String newUsername = "awesome_user";
        server.receiveMessage(session, "/change_login " + newUsername);
        assertTrue(spectatorSession.getLastMsg().contains(newUsername));
        assertFalse(server.loginInUse("user1"));
        assertTrue(server.loginInUse(newUsername));

        assertFalse(server.getLogins().contains("user1"));

        spectatorSession.clearHist();
        server.unRegisterSession(session);
        assertFalse(server.getLogins().contains(newUsername));
        assertFalse(server.getSessions().contains(session));
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
        SimpleChatSession session = new SimpleChatSession("user2");
        server.registerSession(session);
        String message1 = "msg1";
        session.sendMessage(message1);
        assertEquals(session.getLastMsg(), message1);

        String message2 = "msg2";
        session.clearHist();
        spectatorSession.clearHist();
        server.receiveMessage(session, message2);
        assertNotNull(session.getLastMsg());
        assertNotNull(spectatorSession.getLastMsg());
    }

    @Test
    public void testContainsHelpCommand() {
        assertTrue(server.getChatCommandMap().containsKey("help"));
    }

    @Test
    public void testTopic() {
        SimpleChatSession session = new SimpleChatSession("user2");
        server.registerSession(session);

        String topic = "awesome_topic";
        server.receiveMessage(session, "/set_topic " + topic);

        SimpleChatSession session1 = new SimpleChatSession("user2");
        server.registerSession(session1);

        server.receiveMessage(session, "/set_topic ");

        assertTrue(session1.getLastMsg().contains(topic));
    }

    @Test
    public void testExit() {
        SimpleChatSession session = new SimpleChatSession("user3");
        server.registerSession(session);
        server.receiveMessage(session, "/exit");

        server.receiveMessage(session, "/unknown_command");
        assertTrue(session.getLastMsg().toLowerCase().contains("unknown"));

        assertTrue(session.disconnected);
    }

    @Test
    public void testHelp() {
        SimpleChatSession session = new SimpleChatSession("user4");
        server.registerSession(session);
        server.receiveMessage(session, "/help");

        assertTrue(session.getLastMsg().contains("List of available commands"));
    }

    @Test
    public void histTest() {
        for (int i = 1; i <= 150; i++) {
            server.saveAndSendMessage("msg" + String.valueOf(i));
        }
        SimpleChatSession session = new SimpleChatSession("user5");
        server.registerSession(session);
        assertEquals(session.getHist().size(), 100 + 1);
        assertEquals(session.getHist().get(session.getHist().size() - 3), "msg150");
    }


    class SimpleChatSession extends ChatSession {
        boolean disconnected;
        List<String> hist = new ArrayList<>();

        SimpleChatSession(String login) {
            super(login);
        }

        @Override
        public void disconnect() {
            disconnected = true;
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