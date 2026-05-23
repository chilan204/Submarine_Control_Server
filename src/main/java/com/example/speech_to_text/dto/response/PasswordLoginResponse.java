package com.example.speech_to_text.dto.response;

import lombok.*;

@Data
@Builder
public class PasswordLoginResponse {

    private String token;

    private String roleCode;

    private String username;

    private String name;

    private Long userId;

    private long expiresIn;
}