package com.example.submarine_control_server.services;

import com.example.submarine_control_server.dto.request.UserRequest;
import com.example.submarine_control_server.dto.response.UserResponse;
import com.example.submarine_control_server.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserResponse> getAllUser();

    UserResponse getUserById(Long id);

    UserResponse createUser(UserRequest user);

    UserResponse updateUser(Long id, UserRequest updatedUser);

    void deleteUser(Long id);

    Optional<User> findByUsername(String username);

    User findEntityByUsername(String username);
}
