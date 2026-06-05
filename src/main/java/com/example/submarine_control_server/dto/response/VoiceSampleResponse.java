package com.example.submarine_control_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class VoiceSampleResponse {

    private Long userId;

    private String filePath;
}
