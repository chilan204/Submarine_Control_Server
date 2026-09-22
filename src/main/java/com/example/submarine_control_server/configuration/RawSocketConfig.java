package com.example.submarine_control_server.configuration;

import com.example.submarine_control_server.websocket.TelemetryHandler;
import com.example.submarine_control_server.websocket.TelemetryHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class RawSocketConfig implements WebSocketConfigurer {

    private final TelemetryHandler telemetryHandler;
    private final TelemetryHandshakeInterceptor telemetryHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(telemetryHandler, "/ws")
                .addInterceptors(telemetryHandshakeInterceptor)
                .setAllowedOriginPatterns("http://localhost:*");
    }
}
