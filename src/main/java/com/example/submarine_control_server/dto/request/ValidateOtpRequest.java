package com.example.submarine_control_server.dto.request;

import lombok.Data;

@Data
public class ValidateOtpRequest {
    private String username;
    private String otp;
}
