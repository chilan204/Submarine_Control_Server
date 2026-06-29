package com.example.submarine_control_server.services;

import java.io.InputStream;

public interface AIService {
    String processVoice(InputStream inputStream, String language);
    String extractEmbedding(InputStream inputStream);
}