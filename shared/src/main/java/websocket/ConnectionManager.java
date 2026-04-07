package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConnectionManager {
    private Map<Integer, Set<Session>> sessions = new HashMap<>();

    public ConnectionManager(Map<Integer, Set<Session>> sessions) {
        this.sessions = sessions;
    }

    public void add(int gameID, Session session) {
        sessions.put(gameID, session);
    }

    public void remove(Session session) {

    }

    public void broadcastToAll(int gameID, String message) {

    }

    public void broadcastToAllExcept(int gameID, Session excludedSession, String message) {

    }

    public void sendToOne(Session session, String message) {

    }
}
