package com.example.speech_to_text.dto.response;

import lombok.Data;

@Data
public class UserResponse {

    private Long id;

    private String username;

    private String name;

    private String email;

    private String phone;

    private String roleCode;

    private Integer rolePriority;
}