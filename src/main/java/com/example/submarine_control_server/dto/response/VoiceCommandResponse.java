package com.example.submarine_control_server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VoiceCommandResponse {

    private String status;

    @JsonProperty("speaker_id")
    private String speaker;

    @JsonProperty("speaker_score")
    private Double speakerScore;

    @JsonProperty("verification_score")
    private Double verificationScore;

    private Boolean verified;

    private String text;

    private VoiceCommandDetail command;

    private String role;
}
