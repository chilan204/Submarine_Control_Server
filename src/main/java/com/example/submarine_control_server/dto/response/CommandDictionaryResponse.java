package com.example.submarine_control_server.dto.response;

import lombok.Data;

@Data
public class CommandDictionaryResponse {

    private Long id;

    private String keyword;

    private String action;

    private String direction;

    private Boolean hasValue;
}
