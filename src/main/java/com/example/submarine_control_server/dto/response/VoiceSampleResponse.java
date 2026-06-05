package com.example.submarine_control_server.dto.response;

import lombok.Data;

@Data
public class VoiceSampleResponse {

    private Long id;

    private Long userId;

    private String userName;

    private String fileName;

    private Boolean active;

    private String filePath;

    private Double duration;
}
