package com.example.submarine_control_server.mapper;

import com.example.submarine_control_server.dto.response.UserSessionResponse;
import com.example.submarine_control_server.entities.UserSession;
import org.springframework.stereotype.Component;

@Component
public class UserSessionMapper {

    public UserSessionResponse toResponseDTO(UserSession entity) {
        if (entity == null) return null;

        UserSessionResponse dto = new UserSessionResponse();

        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());

        dto.setTranscript(entity.getTranscript());
        dto.setAction(entity.getAction());
        dto.setDirection(entity.getDirection());
        dto.setValue(entity.getValue());

        dto.setSpeaker(entity.getSpeaker());
        dto.setSpeakerScore(entity.getSpeakerScore());

        dto.setVerificationScore(entity.getVerificationScore());
        dto.setVerified(entity.getVerified());

        dto.setRole(entity.getRole());
        dto.setCommandStatus(entity.getCommandStatus());
        dto.setExecuted(entity.getExecuted());

        dto.setCreatedDate(entity.getCreatedDate());

        return dto;
    }
}