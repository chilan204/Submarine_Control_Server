package com.example.speech_to_text.controllers;

import com.example.speech_to_text.dto.common.response.ResponseBase;
import com.example.speech_to_text.dto.common.response.ResponseBaseList;
import com.example.speech_to_text.dto.request.UserRequest;
import com.example.speech_to_text.dto.response.UserResponse;
import com.example.speech_to_text.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ResponseBaseList<UserResponse>> getAllUser() {
        List<UserResponse> list = userService.getAllUser();

        return ResponseEntity.ok(
                ResponseBaseList.<UserResponse>builder()
                        .data(list)
                        .message("Get User list successfully")
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBase<UserResponse>> getUserById(
            @PathVariable Long id
    ) {
        UserResponse dto = userService.getUserById(id);

        return ResponseEntity.ok(
                ResponseBase.<UserResponse>builder()
                        .data(dto)
                        .message("Get User successfully")
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ResponseBase<UserResponse>> createUser(
            @Valid @RequestBody UserRequest userRequest
    ) {
        UserResponse dto = userService.createUser(userRequest);

        return ResponseEntity.ok(
                ResponseBase.<UserResponse>builder()
                        .data(dto)
                        .message("Create User successfully")
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseBase<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest updateUser
    ) {
        UserResponse dto = userService.updateUser(id, updateUser);

        return ResponseEntity.ok(
                ResponseBase.<UserResponse>builder()
                        .data(dto)
                        .message("Update User successfully")
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseBase<Void>> deleteUser(
            @PathVariable Long id
    ) {
        userService.deleteUser(id);

        return ResponseEntity.ok(
                ResponseBase.<Void>builder()
                        .message("Delete User successfully")
                        .build()
        );
    }
}