package org.caloch.core;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class SessionStore {
    private final ConcurrentHashMap<String, SessionItem> sessions;
    private final ThreadLocal<String> currentSessionId=new ThreadLocal<>();

    private SessionStore() {
        this.sessions = new ConcurrentHashMap<>();
    }

    private static SessionStore instance = null;

    public static SessionStore instance() {
        if (instance == null) instance = new SessionStore();
        return instance;
    }

    public HttpPrincipal verify(HttpExchange exchange) throws Exception {
        String sessionId = getSessionId(exchange);
        if (!sessionId.isEmpty()) {
            currentSessionId.set(sessionId);
            SessionItem item=sessions.get(sessionId);
            Date lastUpdatedOn =new Date(item.lastUpdatedOn);
            Calendar calendar= Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTime(lastUpdatedOn);
            if(LocalDateTime.now().minusMinutes(15).toInstant(ZoneOffset.UTC).isAfter(calendar.toInstant())){
                throw new Exception("15 minutes passed since last visit");
            }
            return (HttpPrincipal) item.data.get(sessionId);
        }
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

    public void save(String key, Object data){
        SessionItem item =sessions.get(currentSessionId.get());
        item.getData().put(key, data);
        item.lastUpdatedOn=System.currentTimeMillis();
    }

    public SessionItem newSession(String sessionId,HttpPrincipal principal){
        sessions.put(sessionId, new SessionItem(new HashMap<>(), System.currentTimeMillis()));
        SessionItem sessionItem = sessions.get(sessionId);
        sessionItem.getData().put(sessionId, principal);
        currentSessionId.set(sessionId);
//        response = "New session created: " + sessionId;
//        exchange.getResponseHeaders().add("Set-Cookie", "sessionId=" + sessionId);
        return sessionItem;
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
