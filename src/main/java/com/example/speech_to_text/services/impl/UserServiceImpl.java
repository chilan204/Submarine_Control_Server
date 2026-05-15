package com.example.speech_to_text.services.impl;

import com.example.speech_to_text.dto.request.UserRequest;
import com.example.speech_to_text.dto.response.UserResponse;
import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.mapper.UserMapper;
import com.example.speech_to_text.repositories.UserRepository;
import com.example.speech_to_text.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.*;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

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