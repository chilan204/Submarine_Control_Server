package com.example.submarine_control_server.dto.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArbitrationCommand {

    private Long userId;

    private Integer priority;

    private String command;

    private Long timestamp;
}