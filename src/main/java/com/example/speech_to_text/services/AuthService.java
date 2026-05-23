package com.example.speech_to_text.services;

import com.example.speech_to_text.dto.request.ChangePasswordRequest;
import com.example.speech_to_text.dto.request.UserRequest;
import com.example.speech_to_text.dto.request.ValidateOtpRequest;
import com.example.speech_to_text.dto.response.PasswordLoginResponse;
import com.example.speech_to_text.dto.response.UserResponse;
import com.example.speech_to_text.dto.response.VoiceLoginResponse;

import java.io.InputStream;

public interface AuthService {

    UserResponse register(UserRequest req);

    PasswordLoginResponse passwordLogin(UserRequest req);

    VoiceLoginResponse voiceLogin(InputStream inputStream);

    void logout();

    boolean validateEmail(UserRequest req);

    boolean validateOtp(ValidateOtpRequest req);

    void changePasswordForgot(UserRequest req);

    void changePassword(ChangePasswordRequest req);
}