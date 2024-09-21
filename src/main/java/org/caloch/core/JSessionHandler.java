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