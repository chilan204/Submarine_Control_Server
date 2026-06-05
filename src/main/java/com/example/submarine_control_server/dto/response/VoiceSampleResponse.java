package com.example.submarine_control_server.dto.response;

import lombok.Data;

@Data
public class VoiceSampleResponse {

    private Long userId;

    private Boolean active;

    private String filePath;
}
