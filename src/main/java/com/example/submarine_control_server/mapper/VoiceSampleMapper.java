package com.example.submarine_control_server.mapper;

import com.example.submarine_control_server.dto.response.VoiceSampleResponse;
import com.example.submarine_control_server.entities.VoiceSample;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoiceSampleMapper {

    public VoiceSampleResponse toResponseDTO(VoiceSample entity) {
        if (entity == null) return null;

        VoiceSampleResponse dto = new VoiceSampleResponse();
        dto.setUserId(entity.getUser().getId());
        dto.setActive(entity.getActive());
        dto.setFilePath(entity.getFilePath());

        return dto;
    }
}