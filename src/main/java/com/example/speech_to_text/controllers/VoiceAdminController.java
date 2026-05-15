package com.example.speech_to_text.controllers;

import com.example.speech_to_text.dto.common.response.ResponseBase;
import com.example.speech_to_text.dto.response.VoiceSampleResponse;
import com.example.speech_to_text.services.VoiceSampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/voice-samples")
public class VoiceAdminController {
    private final VoiceSampleService voiceSampleService;

    @Autowired
    public VoiceAdminController(VoiceSampleService voiceSampleService) {
        this.voiceSampleService = voiceSampleService;
    }

    @GetMapping
    public ResponseEntity<ResponseBase<List<VoiceSampleResponse>>> getAllVoiceSamples() {

        List<VoiceSampleResponse> data = voiceSampleService.getAllVoiceSamples();

        return ResponseEntity.ok(
                ResponseBase.<List<VoiceSampleResponse>>builder()
                        .data(data)
                        .message("OK")
                        .build()
        );
    }
}