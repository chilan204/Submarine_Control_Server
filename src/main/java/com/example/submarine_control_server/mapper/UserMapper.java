package com.example.submarine_control_server.mapper;

import com.example.submarine_control_server.dto.request.UserRequest;
import com.example.submarine_control_server.dto.response.UserResponse;
import com.example.submarine_control_server.entities.Role;
import com.example.submarine_control_server.entities.User;
import com.example.submarine_control_server.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final RoleRepository roleRepository;

    public User toEntity(UserRequest dto) {
        if (dto == null) return null;

        Role defaultRole = roleRepository.findByCode("OFFICER_1")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhone(dto.getPhone());
        user.setRole(defaultRole);

        return user;
    }

    public void updateEntity(User entity, UserRequest dto) {
        if (entity == null || dto == null) return;

        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
    }

    public UserResponse toResponseDTO(User entity) {
        if (entity == null) return null;

        UserResponse dto = new UserResponse();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());

        if (entity.getRole() != null) {
            dto.setRoleCode(
                    entity.getRole().getCode()
            );
            dto.setRolePriority(
                    entity.getRole().getPriority()
            );
        }

        return dto;
    }
}