package com.example.submarine_control_server.dto.request;

import lombok.Data;

@Data
public class RoleRequest {
    private String code;
    private Integer priority;
}
