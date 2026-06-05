package com.example.submarine_control_server.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserSessionAdminResponse {

    private Long id;

    private UserResponse user;

    private String transcript;

    private String action;

    private String direction;

    private Integer value;

    private String role;

    private String commandStatus;

    private Boolean executed;

    private LocalDateTime createdDate;
}