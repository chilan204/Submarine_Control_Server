package com.example.speech_to_text.services;

import com.example.speech_to_text.dto.response.VoiceSampleResponse;
import com.example.speech_to_text.entities.VoiceSample;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VoiceSampleService {
    List<VoiceSampleResponse> getAllVoiceSamples();

    void saveVoiceSample(Long userId, MultipartFile file);

    VoiceSample getVoiceSample(Long userId);

    void deleteVoiceSample(Long userId);
}
