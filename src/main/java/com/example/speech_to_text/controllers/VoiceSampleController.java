package com.example.speech_to_text.controllers;

import com.example.speech_to_text.dto.common.response.ResponseBase;
import com.example.speech_to_text.entities.VoiceSample;
import com.example.speech_to_text.services.VoiceSampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/voice/{userId}")
public class VoiceSampleController {
    private final VoiceSampleService voiceSampleService;

    @Autowired
    public VoiceSampleController(VoiceSampleService voiceSampleService) {
        this.voiceSampleService = voiceSampleService;
    }

    @GetMapping
    public ResponseEntity<ResponseBase<VoiceSample>> getVoiceSample(
            @PathVariable Long userId
    ) {
        VoiceSample sample = voiceSampleService.getVoiceSample(userId);

        return ResponseEntity.ok(
                ResponseBase.<VoiceSample>builder()
                        .data(sample)
                        .message("Get voice sample successfully")
                        .build()
        );
    }

    @PutMapping
    public ResponseEntity<ResponseBase<String>> uploadVoice(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseBase.<String>builder()
                            .message("Invalid file")
                            .build()
            );
        }

        voiceSampleService.saveVoiceSample(userId, file);

        try {
            RestTemplate restTemplate = new RestTemplate();

            restTemplate.postForEntity(
                    "http://localhost:5000/reload-cache",
                    null,
                    String.class
            );

        } catch (Exception e) {
            System.out.println("Cannot notify AI service: " + e.getMessage());
        }

        return ResponseEntity.ok(
                ResponseBase.<String>builder()
                        .data("Uploaded successfully")
                        .message("Voice sample uploaded")
                        .build()
        );
    }

    @DeleteMapping
    public ResponseEntity<ResponseBase<String>> deleteVoiceSample(
            @PathVariable Long userId
    ) {
        voiceSampleService.deleteVoiceSample(userId);

        return ResponseEntity.ok(
                ResponseBase.<String>builder()
                        .data("Deleted")
                        .message("Voice sample deleted successfully")
                        .build()
        );
    }
}