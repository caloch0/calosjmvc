package org.caloch.core;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class SessionStore {
    private final ConcurrentHashMap<String, SessionItem> sessions;
    private final ThreadLocal<String> currentSessionId=new ThreadLocal<>();

    private SessionStore() {
        this.sessions = new ConcurrentHashMap<>();
    }

    private static SessionStore instance = null;

    public static SessionStore instance() throws IOException {
        if (instance == null) instance = new SessionStore();
        return instance;
    }

    public String verify(HttpExchange exchange) throws IOException {
        String response;
        String sessionId = getSessionId(exchange);
        if (sessionId == null) {
            // Create a new session if no session ID is found
            sessionId = UUID.randomUUID().toString();
            HttpPrincipal principal = exchange.getPrincipal();
            String s = "Session data for " + sessionId;
            sessions.put(sessionId, new SessionItem(new HashMap<>(), System.currentTimeMillis()));
            SessionItem sessionItem = sessions.get(sessionId);
            sessionItem.getData().put(sessionId, principal);
            currentSessionId.set(sessionId);
            response = "New session created: " + sessionId;
            exchange.getResponseHeaders().add("Set-Cookie", "sessionId=" + sessionId);
        } else {
            // Retrieve session data
            response = "Session ID: " + sessionId + ", Data: " + sessions.get(sessionId);
        }

        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
        return null;
    }


    private String getSessionId(HttpExchange exchange) {
        String cookie = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookie != null && cookie.startsWith("sessionId=")) {
            return cookie.substring("sessionId=".length());
        }
        return null;
    }

    public Object get(String key) {
        return sessions.get(currentSessionId.get()).getData().get(key);
    }

    interface ISessionStore{
        void save(String key,Object data);
        Object get(String key);
        String verify(HttpExchange exchange);
        SessionItem newSession(String sessionId);
    }

    static class SessionItem {

        public SessionItem(HashMap<String,Object> data, long lastUpdatedOn) {
            this.data = data;
            this.lastUpdatedOn = lastUpdatedOn;
        }


        public Map<String, Object> getData() {
            return data;
        }

        public Map<String,Object> data;
        long lastUpdatedOn;
    }
}
