package com.example.submarine_control_server.controllers;

import com.example.submarine_control_server.dto.common.response.ResponseBase;
import com.example.submarine_control_server.dto.response.VoiceCommandResponse;
import com.example.submarine_control_server.services.VoiceCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/voice-command")
public class VoiceCommandController {

    private final VoiceCommandService voiceCommandService;

    @PostMapping
    public ResponseEntity<ResponseBase<VoiceCommandResponse>> handle(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "language", required = false) String language
    ) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseBase.<VoiceCommandResponse>builder()
                            .message("Invalid file")
                            .build()
            );
        }

        try {
            VoiceCommandResponse response = voiceCommandService.handleVoiceCommand(file, language);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        ResponseBase.<VoiceCommandResponse>builder()
                                .message("User not found")
                                .build()
                );
            }

            String status = response.getStatus();

            if ("SPEAKER_VERIFICATION_FAILED".equals(status)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ResponseBase.<VoiceCommandResponse>builder()
                                .data(response)
                                .message("Speaker verification failed")
                                .build()
                );
            }

            if ("INVALID_COMMAND".equals(status)) {
                return ResponseEntity.badRequest().body(
                        ResponseBase.<VoiceCommandResponse>builder()
                                .data(response)
                                .message("Cannot extract command")
                                .build()
                );
            }

            if ("ROLE_DENIED".equals(status)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ResponseBase.<VoiceCommandResponse>builder()
                                .data(response)
                                .message("Role permission denied")
                                .build()
                );
            }

            if ("EXECUTED".equals(status)) {
                return ResponseEntity.ok(
                        ResponseBase.<VoiceCommandResponse>builder()
                                .data(response)
                                .message("Success")
                                .build()
                );
            }

            // For other arbitration statuses (e.g. BLOCKED, PENDING, etc.)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ResponseBase.<VoiceCommandResponse>builder()
                            .data(response)
                            .message("Rejected by arbitration")
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    ResponseBase.<VoiceCommandResponse>builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }
}