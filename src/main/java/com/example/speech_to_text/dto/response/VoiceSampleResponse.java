package com.example.speech_to_text.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoiceSampleResponse {

    private Long userId;

    private String filePath;
}
