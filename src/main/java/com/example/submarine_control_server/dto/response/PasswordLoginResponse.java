package com.example.submarine_control_server.dto.response;

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