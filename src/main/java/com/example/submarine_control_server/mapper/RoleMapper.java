package com.example.submarine_control_server.mapper;

import com.example.submarine_control_server.dto.request.RoleRequest;
import com.example.submarine_control_server.dto.response.RoleResponse;
import com.example.submarine_control_server.entities.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleMapper {
    
    public Role toEntity(RoleRequest dto) {
        if (dto == null) return null;

        Role role = new Role();
        role.setCode(dto.getCode());
        role.setPriority(dto.getPriority());

        return role;
    }

    public RoleResponse toResponseDTO(Role entity) {
        if (entity == null) return null;

        RoleResponse dto = new RoleResponse();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setPriority(entity.getPriority());

        return dto;
    }
}