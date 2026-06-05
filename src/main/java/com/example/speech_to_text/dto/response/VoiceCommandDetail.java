package com.example.speech_to_text.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class VoiceCommandDetail {

    private String action;

    private String direction;

    private Integer value;

    public String toCommandText() {

        StringBuilder sb = new StringBuilder();

        if (action != null) {
            sb.append(action);
        }

        if (direction != null) {
            sb.append("_").append(direction);
        }

        if (value != null) {
            sb.append("_").append(value);
        }

        return sb.toString();
    }
}
