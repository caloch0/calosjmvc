package org.caloch.core;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class SessionStore {
    private final HttpExchange exchange;
    private final ConcurrentHashMap<String, SessionItem> sessions;

    private SessionStore(HttpExchange exchange) throws IOException {
        this.exchange = exchange;
        this.sessions = new ConcurrentHashMap<>();

        String response;
        String sessionId = getSessionId(exchange);

        if (sessionId == null) {
            // Create a new session if no session ID is found
            sessionId = UUID.randomUUID().toString();
            exchange.getPrincipal();
            sessions.put(sessionId, new SessionItem("Session data for " + sessionId, System.currentTimeMillis()));
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

    }

    private static SessionStore instance = null;

    public static SessionStore instance(HttpExchange exchange) throws IOException {
        if (instance == null) instance = new SessionStore(exchange);
        return instance;
    }


    private String getSessionId(HttpExchange exchange) {
        String cookie = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookie != null && cookie.startsWith("sessionId=")) {
            return cookie.substring("sessionId=".length());
        }
        return null;
    }

    interface ISessionStore{
        void save(String key,Object data);
        Object get(String key);
        String verify(HttpExchange exchange);
        SessionItem newSession(String sessionId);
    }

    static class SessionItem {

        public SessionItem(Object data, long lastUpdatedOn) {
            this.data = data;
            this.lastUpdatedOn = lastUpdatedOn;
        }

        public Object data;
        long lastUpdatedOn;
    }
}
