package com.example.submarine_control_server.services.impl;

import com.example.submarine_control_server.dto.request.UserRequest;
import com.example.submarine_control_server.dto.response.UserResponse;
import com.example.submarine_control_server.entities.User;
import com.example.submarine_control_server.mapper.UserMapper;
import com.example.submarine_control_server.repositories.UserRepository;
import com.example.submarine_control_server.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> getAllUser() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponse createUser(UserRequest userDTO) {
        User user = userMapper.toEntity(userDTO);
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest updatedUserDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateEntity(user, updatedUserDTO);

        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}