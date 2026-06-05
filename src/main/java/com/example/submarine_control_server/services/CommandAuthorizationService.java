package com.example.submarine_control_server.services;

import com.example.submarine_control_server.dto.response.VoiceCommandDetail;
import com.example.submarine_control_server.entities.Role;

public interface CommandAuthorizationService {

    boolean isAllowed(
            Role role,
            VoiceCommandDetail command
    );
}