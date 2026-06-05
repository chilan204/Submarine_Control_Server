package com.example.submarine_control_server.dto.response;

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