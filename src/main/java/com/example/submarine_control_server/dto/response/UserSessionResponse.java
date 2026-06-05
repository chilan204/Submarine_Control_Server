package com.example.submarine_control_server.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserSessionResponse {

    private Long id;

    private Long userId;

    private String transcript;

    private String action;

    private String direction;

    private Integer value;

    private String speaker;

    private Double speakerScore;

    private Double verificationScore;

    private Boolean verified;

    private String role;

    private String commandStatus;

    private Boolean executed;

    private LocalDateTime createdDate;
}