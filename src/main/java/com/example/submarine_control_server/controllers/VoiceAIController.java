package com.example.submarine_control_server.controllers;

import com.example.submarine_control_server.dto.common.response.ResponseBaseList;
import com.example.submarine_control_server.dto.response.VoiceSampleResponse;
import com.example.submarine_control_server.services.VoiceSampleService;
import com.example.submarine_control_server.security.InternalApiAccessValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/voice-admin")
public class VoiceAIController {
    private final VoiceSampleService voiceSampleService;
    private final InternalApiAccessValidator internalApiAccessValidator;

    @Autowired
    public VoiceAIController(
            VoiceSampleService voiceSampleService,
            InternalApiAccessValidator internalApiAccessValidator) {
        this.voiceSampleService = voiceSampleService;
        this.internalApiAccessValidator = internalApiAccessValidator;
    }

    @GetMapping
    public ResponseEntity<ResponseBaseList<VoiceSampleResponse>> getActiveVoiceSamples(
            HttpServletRequest request) {
        internalApiAccessValidator.validate(request);

        List<VoiceSampleResponse> data = voiceSampleService.getActiveVoiceSamples();

        return ResponseEntity.ok(
                ResponseBaseList.<VoiceSampleResponse>builder()
                        .data(data)
                        .message("OK")
                        .build()
        );
    }
}
