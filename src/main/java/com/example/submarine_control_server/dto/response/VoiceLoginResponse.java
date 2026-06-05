package com.example.submarine_control_server.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoiceLoginResponse {

    private Boolean authenticated;

    private String token;

    private Long userId;

    private String username;

    private String name;

    private String roleCode;

    private String speaker;

    private Double verificationScore;

    private String text;
}