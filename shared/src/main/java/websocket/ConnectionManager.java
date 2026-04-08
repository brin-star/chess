package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.*;

public class ConnectionManager {
    private Map<Integer, Set<Session>> sessions;

    public ConnectionManager() {
        sessions = new HashMap<>();
    }

    public void add(int gameID, Session session) {
        if (!sessions.containsKey(gameID)) {
            sessions.put(gameID, new HashSet<>());
        }
        sessions.get(gameID).add(session);
    }

    public void remove(Session session) {
        for (Set<Session> set : sessions.values()) {
            if (set.contains(session)) {
                set.remove(session);
                break;
            }
        }
    }

    public void broadcastToAll(int gameID, String message) {
        Set<Session> set = sessions.get(gameID);
        if (set != null) {
            for (Session session : set) {
                sendToOne(session, message);
            }
        }
    }

    public void broadcastToAllExcept(int gameID, Session excludedSession, String message) {

    }

    public void sendToOne(Session session, String message) {

    }
}
