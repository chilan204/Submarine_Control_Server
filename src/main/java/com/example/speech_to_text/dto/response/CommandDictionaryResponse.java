package com.example.speech_to_text.dto.response;

import lombok.Data;

@Data
public class CommandDictionaryResponse {

    private Long id;

    private String keyword;

    private String action;

    private String direction;

    private Boolean hasValue;
}
