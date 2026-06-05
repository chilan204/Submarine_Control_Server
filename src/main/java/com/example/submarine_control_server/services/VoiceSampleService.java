package com.example.submarine_control_server.services;

import com.example.submarine_control_server.dto.response.VoiceSampleResponse;
import com.example.submarine_control_server.entities.VoiceSample;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VoiceSampleService {
    List<VoiceSampleResponse> getActiveVoiceSamples();

    List<VoiceSampleResponse> getAllVoiceSamples();

    void saveVoiceSample(Long userId, MultipartFile file);

    VoiceSample getVoiceSample(Long userId);

    void deleteVoiceSample(Long userId);

    void toggleActive(Long userId);
}
