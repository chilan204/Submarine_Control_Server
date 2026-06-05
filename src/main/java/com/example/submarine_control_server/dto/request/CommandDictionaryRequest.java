package com.example.submarine_control_server.dto.request;

import lombok.Data;

@Data
public class CommandDictionaryRequest {
    private String keyword;

    private String action;

    private String direction;

    private Boolean hasValue;

    private Boolean active;
}
