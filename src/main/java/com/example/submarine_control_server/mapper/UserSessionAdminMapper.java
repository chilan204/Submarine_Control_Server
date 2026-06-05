package com.example.submarine_control_server.mapper;

import com.example.submarine_control_server.dto.response.UserSessionAdminResponse;
import com.example.submarine_control_server.entities.UserSession;
import org.springframework.stereotype.Component;

@Component
public class UserSessionAdminMapper {

    private final UserMapper userMapper;

    public UserSessionAdminMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UserSessionAdminResponse toResponseDTO(UserSession entity) {
        if (entity == null) return null;

        UserSessionAdminResponse dto = new UserSessionAdminResponse();

        dto.setId(entity.getId());
        dto.setUser(userMapper.toResponseDTO(entity.getUser()));

        dto.setTranscript(entity.getTranscript());
        dto.setAction(entity.getAction());
        dto.setDirection(entity.getDirection());
        dto.setValue(entity.getValue());

        dto.setRole(entity.getRole());
        dto.setCommandStatus(entity.getCommandStatus());
        dto.setExecuted(entity.getExecuted());

        dto.setCreatedDate(entity.getCreatedDate());

        return dto;
    }
}