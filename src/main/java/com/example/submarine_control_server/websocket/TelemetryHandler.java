package com.example.submarine_control_server.websocket;

import lombok.NonNull;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WebSocket handler for real-time submarine telemetry.
 * <p>
 * Accepts JSON messages with the following format:
 * <pre>
 * {
 *   "latitude": 10.82,
 *   "longitude": 108.20,
 *   "depth": -35,
 *   "heading": 60,
 *   "speed": 4.2,
 *   "timestamp": "2026-05-27T22:00:00Z"
 * }
 * </pre>
 * Broadcasts received telemetry to all connected clients (Flutter, web, etc.).
 */
@Component
@RequiredArgsConstructor
public class TelemetryHandler extends TextWebSocketHandler {

    private static final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        sessions.add(session);
        System.out.println("[TelemetryHandler] Client connected: " + session.getId()
                + " | Total: " + sessions.size());
    }

    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        if (!"producer".equals(session.getAttributes().get("telemetryRole"))) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        validateTelemetry(message.getPayload());

        // Broadcast telemetry to all connected clients
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                s.sendMessage(message);
            }
        }
    }

    private void validateTelemetry(String payload) throws Exception {
        JsonNode node = objectMapper.readTree(payload);
        requireRange(node, "latitude", -90, 90);
        requireRange(node, "longitude", -180, 180);
        requireRange(node, "depth", -12000, 0);
        requireRange(node, "heading", 0, 360);
        requireRange(node, "speed", 0, 100);
    }

    private void requireRange(JsonNode node, String field, double min, double max) {
        JsonNode value = node.get(field);
        if (value == null || !value.isNumber()
                || !Double.isFinite(value.asDouble())
                || value.asDouble() < min
                || value.asDouble() > max) {
            throw new IllegalArgumentException("Invalid telemetry field: " + field);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        sessions.remove(session);
        System.out.println("[TelemetryHandler] Client disconnected: " + session.getId()
                + " (" + status + ") | Remaining: " + sessions.size());
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) {
        System.err.println("[TelemetryHandler] Transport error for " + session.getId()
                + ": " + exception.getMessage());
        sessions.remove(session);
    }
}
