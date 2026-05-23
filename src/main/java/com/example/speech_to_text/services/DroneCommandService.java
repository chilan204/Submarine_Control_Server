package com.example.speech_to_text.services;

import com.example.speech_to_text.dto.response.VoiceCommandDetail;

public interface DroneCommandService {

    void execute(VoiceCommandDetail command);
}