package com.example.speech_to_text.configuration;

import com.example.speech_to_text.websocket.TelemetryHandler;
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
