package com.example.submarine_control_server.services;

import com.example.submarine_control_server.dto.response.VoiceCommandResponse;
import org.springframework.web.multipart.MultipartFile;

public interface VoiceCommandService {
    VoiceCommandResponse handleVoiceCommand(MultipartFile file, String language);
}
