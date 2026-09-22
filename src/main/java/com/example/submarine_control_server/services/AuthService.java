package com.example.submarine_control_server.services;

import com.example.submarine_control_server.dto.request.ChangePasswordRequest;
import com.example.submarine_control_server.dto.request.UserRequest;
import com.example.submarine_control_server.dto.response.PasswordLoginResponse;
import com.example.submarine_control_server.dto.response.UserResponse;
import com.example.submarine_control_server.dto.response.VoiceLoginResponse;

import java.io.InputStream;

public interface AuthService {

    UserResponse register(UserRequest req);

    PasswordLoginResponse passwordLogin(UserRequest req);

    VoiceLoginResponse voiceLogin(InputStream inputStream,  String language);

    void logout();

    void changePassword(ChangePasswordRequest req);
}
