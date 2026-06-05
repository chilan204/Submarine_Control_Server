package com.example.speech_to_text.dto.request;

import lombok.Data;

@Data
public class CommandDictionaryRequest {
    private String keyword;

    private String action;

    private String direction;

    private Boolean hasValue;
}
