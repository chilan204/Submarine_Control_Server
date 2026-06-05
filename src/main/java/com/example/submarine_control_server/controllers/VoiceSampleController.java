package com.example.submarine_control_server.controllers;

import com.example.submarine_control_server.dto.common.response.ResponseBase;
import com.example.submarine_control_server.dto.common.response.ResponseBaseList;
import com.example.submarine_control_server.dto.response.VoiceSampleResponse;
import com.example.submarine_control_server.entities.VoiceSample;
import com.example.submarine_control_server.services.VoiceSampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/voice-samples")
public class VoiceSampleController {
    private final VoiceSampleService voiceSampleService;

    @Autowired
    public VoiceSampleController(VoiceSampleService voiceSampleService) {
        this.voiceSampleService = voiceSampleService;
    }

    @GetMapping
    public ResponseEntity<ResponseBaseList<VoiceSampleResponse>> getAllVoiceSamples() {

        List<VoiceSampleResponse> list = voiceSampleService.getAllVoiceSamples();

        return ResponseEntity.ok(
                ResponseBaseList.<VoiceSampleResponse>builder()
                        .data(list)
                        .message("Get VoiceSample list successfully")
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBase<VoiceSample>> getVoiceSample(
            @PathVariable Long id
    ) {
        VoiceSample dto = voiceSampleService.getVoiceSample(id);

        return ResponseEntity.ok(
                ResponseBase.<VoiceSample>builder()
                        .data(dto)
                        .message("Get voice sample successfully")
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseBase<String>> uploadVoice(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseBase.<String>builder()
                            .message("Invalid file")
                            .build()
            );
        }

        voiceSampleService.saveVoiceSample(id, file);

        notifyAiReloadSpeakerCache();

        return ResponseEntity.ok(
                ResponseBase.<String>builder()
                        .data("Uploaded successfully")
                        .message("Voice sample uploaded")
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseBase<String>> deleteVoiceSample(
            @PathVariable Long id
    ) {
        voiceSampleService.deleteVoiceSample(id);

        notifyAiReloadSpeakerCache();

        return ResponseEntity.ok(
                ResponseBase.<String>builder()
                        .data("Deleted")
                        .message("Voice sample deleted successfully")
                        .build()
        );
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ResponseBase<String>> toggleActive(
            @PathVariable Long id
    ) {
        voiceSampleService.toggleActive(id);
        
        notifyAiReloadSpeakerCache();

        return ResponseEntity.ok(
                ResponseBase.<String>builder()
                        .data("Toggled successfully")
                        .message("Voice sample status toggled")
                        .build()
        );
    }

    private void notifyAiReloadSpeakerCache() {
        try {
            RestTemplate restTemplate = new RestTemplate();

            restTemplate.postForEntity(
                    "http://localhost:5000/reload-speaker-cache",
                    null,
                    String.class
            );

        } catch (Exception e) {
            System.out.println("Cannot notify AI service: " + e.getMessage());
        }
    }
}