package com.example.submarine_control_server.services;

import com.example.submarine_control_server.dto.request.RoleRequest;
import com.example.submarine_control_server.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    List<RoleResponse> getAllRole();

    RoleResponse getRoleById(Long id);

    RoleResponse createRole(RoleRequest role);

    RoleResponse updateRole(Long id, RoleRequest updatedRole);

    void deleteRole(Long id);
}
