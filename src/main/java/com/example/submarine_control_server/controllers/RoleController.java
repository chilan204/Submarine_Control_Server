package com.example.submarine_control_server.controllers;

import com.example.submarine_control_server.dto.common.response.ResponseBase;
import com.example.submarine_control_server.dto.common.response.ResponseBaseList;
import com.example.submarine_control_server.dto.request.RoleRequest;
import com.example.submarine_control_server.dto.response.RoleResponse;
import com.example.submarine_control_server.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<ResponseBaseList<RoleResponse>> getAllRole() {

        List<RoleResponse> list = roleService.getAllRole();

        return ResponseEntity.ok(
                ResponseBaseList.<RoleResponse>builder()
                        .data(list)
                        .message("Get role list successfully")
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBase<RoleResponse>> getRoleById(
            @PathVariable Long id
    ) {
        RoleResponse dto = roleService.getRoleById(id);

        return ResponseEntity.ok(
                ResponseBase.<RoleResponse>builder()
                        .data(dto)
                        .message("Get role successfully")
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ResponseBase<RoleResponse>> createRole(
            @RequestBody RoleRequest role
    ) {
        RoleResponse dto = roleService.createRole(role);

        return ResponseEntity.ok(
                ResponseBase.<RoleResponse>builder()
                        .data(dto)
                        .message("Create role successfully")
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseBase<RoleResponse>> updateRole(
            @PathVariable Long id,
            @RequestBody RoleRequest updateRole
    ) {
        RoleResponse dto = roleService.updateRole(id, updateRole);

        return ResponseEntity.ok(
                ResponseBase.<RoleResponse>builder()
                        .data(dto)
                        .message("Update role successfully")
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseBase<Void>> deleteRole(
            @PathVariable Long id
    ) {
        roleService.deleteRole(id);

        return ResponseEntity.ok(
                ResponseBase.<Void>builder()
                        .message("Delete role successfully")
                        .build()
        );
    }
}