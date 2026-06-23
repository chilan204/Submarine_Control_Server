package com.example.submarine_control_server.services;

import com.example.submarine_control_server.dto.response.VoiceCommandDetail;

public interface AuvCommandService {

    void execute(VoiceCommandDetail command);
}