package org.caloch.core;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
1. convert this class into a session store singleton
2. when user logs in, when user authentication is successful, response with the sessionid cookie
3. when user visits again, try to get the sessionid cookie from request, and try to find the session object from the session store
4. every time user requests, check and set session lastUpdatedOn using timestamp, the session duration between two requests is set
15 minutes minor here by default for now, or set to config, user can set sessionItem Data i.e. sessionData

SessionStore[sessionId,SessionItem],  sessionItem{Object Data, long lastVisited}

 */
public class JSessionHandler {

    // Map to store session IDs and their associated data
    private static Map<String, String> sessions = new HashMap<>();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/session", new SessionHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on port 8000");
    }

    static class SessionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response;
            String sessionId = getSessionId(exchange);

            if (sessionId == null) {
                // Create a new session if no session ID is found
                sessionId = UUID.randomUUID().toString();
                sessions.put(sessionId, "Session data for " + sessionId);
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

        private String getSessionId(HttpExchange exchange) {
            String cookie = exchange.getRequestHeaders().getFirst("Cookie");
            if (cookie != null && cookie.startsWith("sessionId=")) {
                return cookie.substring("sessionId=".length());
            }
            return null;
        }
    }
}