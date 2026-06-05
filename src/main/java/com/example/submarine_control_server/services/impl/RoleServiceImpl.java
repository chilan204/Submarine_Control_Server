package com.example.submarine_control_server.services.impl;

import com.example.submarine_control_server.dto.request.RoleRequest;
import com.example.submarine_control_server.dto.response.RoleResponse;
import com.example.submarine_control_server.entities.Role;
import com.example.submarine_control_server.mapper.RoleMapper;
import com.example.submarine_control_server.repositories.RoleRepository;
import com.example.submarine_control_server.services.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public List<RoleResponse> getAllRole() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findById(id).orElse(null);
        return role != null ? roleMapper.toResponseDTO(role) : null;
    }

    @Override
    public RoleResponse createRole(RoleRequest roleDTO) {
        Role role = roleMapper.toEntity(roleDTO);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toResponseDTO(savedRole);
    }

    @Override
    public RoleResponse updateRole(Long id, RoleRequest updatedRoleDTO) {
        Role role = roleRepository.findById(id).orElse(null);
        if (role != null) {
            role.setCode(updatedRoleDTO.getCode());
            role.setPriority(updatedRoleDTO.getPriority());
            Role updatedRole = roleRepository.save(role);
            return roleMapper.toResponseDTO(updatedRole);
        }
        return null;
    }

    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}