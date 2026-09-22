package com.example.submarine_control_server.websocket;

import com.example.submarine_control_server.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TelemetryHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Value("${app.telemetry.ingest-token:}")
    private String ingestToken;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        MultiValueMap<String, String> params = UriComponentsBuilder
                .fromUri(request.getURI())
                .build()
                .getQueryParams();

        String producerToken = params.getFirst("ingest_token");
        if (producerToken != null && !ingestToken.isBlank()
                && MessageDigest.isEqual(
                producerToken.getBytes(StandardCharsets.UTF_8),
                ingestToken.getBytes(StandardCharsets.UTF_8))) {
            attributes.put("telemetryRole", "producer");
            return true;
        }

        String accessToken = params.getFirst("access_token");
        if (accessToken == null || accessToken.isBlank()) {
            return false;
        }

        try {
            String username = jwtUtil.extractUsername(accessToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!jwtUtil.validateToken(accessToken, userDetails)) {
                return false;
            }
            attributes.put("telemetryRole", "consumer");
            attributes.put("username", username);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // No post-handshake action required.
    }
}
