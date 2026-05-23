package com.example.speech_to_text.services;

import com.example.speech_to_text.dto.response.VoiceCommandDetail;
import com.example.speech_to_text.entities.Role;

public interface CommandAuthorizationService {

    boolean isAllowed(
            Role role,
            VoiceCommandDetail command
    );
}