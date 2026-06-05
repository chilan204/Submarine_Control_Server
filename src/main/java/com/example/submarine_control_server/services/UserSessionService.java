package com.example.submarine_control_server.services;

import com.example.submarine_control_server.dto.response.UserSessionAdminResponse;
import com.example.submarine_control_server.dto.response.UserSessionResponse;
import com.example.submarine_control_server.dto.response.VoiceCommandResponse;
import com.example.submarine_control_server.entities.User;

import java.util.List;

public interface UserSessionService {
    List<UserSessionAdminResponse> getAllUserSession();

    UserSessionResponse getUserSessionById(Long id);

    List<UserSessionResponse> getUserSessionByUserId(Long userId);

    void createFromAIResponse(User user, VoiceCommandResponse response);

    void deleteUserSession(Long id);
}