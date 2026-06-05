package com.example.submarine_control_server.configuration;

import com.example.submarine_control_server.websocket.TelemetryHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class RawSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(new TelemetryHandler(), "/ws")
                .setAllowedOrigins("*");
    }
}
