package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(String id) {
        return SESSIONS.get(id);
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }
}