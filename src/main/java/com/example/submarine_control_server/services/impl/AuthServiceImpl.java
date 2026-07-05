package com.example.submarine_control_server.services.impl;

import com.example.submarine_control_server.dto.request.ChangePasswordRequest;
import com.example.submarine_control_server.dto.request.UserRequest;
import com.example.submarine_control_server.dto.request.ValidateOtpRequest;
import com.example.submarine_control_server.dto.response.PasswordLoginResponse;
import com.example.submarine_control_server.dto.response.UserResponse;
import com.example.submarine_control_server.dto.response.VoiceLoginResponse;
import com.example.submarine_control_server.entities.Role;
import com.example.submarine_control_server.entities.User;
import com.example.submarine_control_server.mapper.UserMapper;
import com.example.submarine_control_server.repositories.RoleRepository;
import com.example.submarine_control_server.repositories.UserRepository;
import com.example.submarine_control_server.security.JwtUtil;
import com.example.submarine_control_server.services.AIService;
import com.example.submarine_control_server.services.AuthService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AIService aiService;
    private final ObjectMapper objectMapper;

    @Override
    public UserResponse register(UserRequest req) {

        if (userRepository.findByUsername(
                req.getUsername().toLowerCase()
        ).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        Role defaultRole =
                roleRepository.findByCode("OFFICER_1")
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Default role not found"
                                )
                        );

        User user = new User();

        user.setUsername(req.getUsername().toLowerCase());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setPhone(req.getPhone());
        user.setRole(defaultRole);

        User savedUser = userRepository.save(user);

        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    public PasswordLoginResponse passwordLogin(UserRequest req) {

        User user = userRepository.findByUsername(
                req.getUsername().toLowerCase()
        ).orElseThrow(() ->
                new RuntimeException("User not found")
        );

        if (!passwordEncoder.matches(
                req.getPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException("Invalid password");
        }

        String roleCode =
                user.getRole() != null
                        ? user.getRole().getCode()
                        : "UNKNOWN";

        String token = jwtUtil.generateToken(
                user.getUsername(),
                roleCode
        );

        return PasswordLoginResponse.builder()
                .token(token)
                .roleCode(roleCode)
                .username(user.getUsername())
                .name(user.getName())
                .userId(user.getId())
                .build();
    }

    @Override
    public VoiceLoginResponse voiceLogin(InputStream inputStream, String language) {
        try {
            String json = aiService.processVoice(inputStream, language);

            if (json == null || json.isBlank()) {
                throw new RuntimeException("Empty AI response");
            }

            JsonNode node = objectMapper.readTree(json);

            // FIX: đúng field name từ AI
            String speaker = node.path("speaker_id").asText();
            double verificationScore = node.path("verification_score").asDouble();
            String text = node.path("text").asText();

            if (speaker == null || speaker.isBlank()) {
                throw new RuntimeException("Invalid speaker from AI");
            }

            if (verificationScore < 0.45) {
                throw new RuntimeException("Speaker verification failed");
            }

            long userId;
            try {
                userId = Long.parseLong(speaker.trim());
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid speaker format: " + speaker);
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Speaker user not found"));

            String roleCode = user.getRole().getCode();
            String token = jwtUtil.generateToken(user.getUsername(), roleCode);

            return VoiceLoginResponse.builder()
                    .authenticated(true)
                    .token(token)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .name(user.getName())
                    .roleCode(roleCode)
                    .speaker(speaker)
                    .verificationScore(verificationScore)
                    .text(text)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Voice login failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void logout() {
    }

    @Override
    public boolean validateEmail(UserRequest req) {

        User user = userRepository.findByUsername(
                req.getUsername().toLowerCase()
        ).orElseThrow(() ->
                new RuntimeException("User not found")
        );

        return req.getEmail().equals(user.getEmail());
    }

    @Override
    public boolean validateOtp(ValidateOtpRequest req) {

        userRepository.findByUsername(
                req.getUsername().toLowerCase()
        ).orElseThrow(() ->
                new RuntimeException("User not found")
        );

        return "1111".equals(req.getOtp());
    }

    @Override
    public void changePasswordForgot(UserRequest req) {

        User user = userRepository.findByUsername(
                req.getUsername().toLowerCase()
        ).orElseThrow(() ->
                new RuntimeException("User not found")
        );

        user.setPassword(
                passwordEncoder.encode(req.getPassword())
        );

        userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest req) {

        User user = userRepository.findByUsername(
                req.getUsername().toLowerCase()
        ).orElseThrow(() ->
                new RuntimeException("User not found")
        );

        if (!passwordEncoder.matches(
                req.getOldPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException(
                    "Old password is incorrect"
            );
        }

        user.setPassword(
                passwordEncoder.encode(req.getNewPassword())
        );

        userRepository.save(user);
    }
}