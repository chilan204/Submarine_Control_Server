package com.example.speech_to_text.dto.command;

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