package com.example.submarine_control_server.controllers;

import com.example.submarine_control_server.dto.common.response.ResponseBase;
import com.example.submarine_control_server.dto.request.ChangePasswordRequest;
import com.example.submarine_control_server.dto.request.UserRequest;
import com.example.submarine_control_server.dto.request.ValidateOtpRequest;
import com.example.submarine_control_server.dto.response.PasswordLoginResponse;
import com.example.submarine_control_server.dto.response.UserResponse;
import com.example.submarine_control_server.dto.response.VoiceLoginResponse;
import com.example.submarine_control_server.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseBase<UserResponse>> register(@RequestBody UserRequest req) {
        UserResponse res = authService.register(req);

        return ResponseEntity.ok(
                ResponseBase.<UserResponse>builder()
                        .data(res)
                        .message("Register successfully")
                        .build()
        );
    }

    @PostMapping("/password-login")
    public ResponseEntity<ResponseBase<PasswordLoginResponse>> login(@RequestBody UserRequest req) {
        PasswordLoginResponse res = authService.passwordLogin(req);

        return ResponseEntity.ok(
                ResponseBase.<PasswordLoginResponse>builder()
                        .data(res)
                        .message("Login successfully")
                        .build()
        );
    }

    @PostMapping("/voice-login")
    public ResponseEntity<ResponseBase<VoiceLoginResponse>> voiceLogin(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "language", required = false) String language
    ) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseBase.<VoiceLoginResponse>builder()
                            .message("Invalid audio file")
                            .build()
            );
        }

        try (InputStream is = file.getInputStream()) {
            VoiceLoginResponse response = authService.voiceLogin(is, language);
            return ResponseEntity.ok(
                    ResponseBase.<VoiceLoginResponse>builder()
                            .data(response)
                            .message("Voice login success")
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    ResponseBase.<VoiceLoginResponse>builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        authService.logout();

        return ResponseEntity.ok(
                Map.of("message", "Logout successfully")
        );
    }

    @PostMapping("/forgot-password/validate-email")
    public ResponseEntity<ResponseBase<Map<String, Object>>> validateEmail(@RequestBody UserRequest req) {
        boolean result = authService.validateEmail(req);

        return ResponseEntity.ok(
                ResponseBase.<Map<String, Object>>builder()
                        .data(Map.of("result", result))
                        .message("Validate email successfully")
                        .build()
        );
    }

    @PostMapping("/forgot-password/validate-otp")
    public ResponseEntity<ResponseBase<Map<String, Object>>> validateOtp(@RequestBody ValidateOtpRequest req) {
        boolean result = authService.validateOtp(req);

        return ResponseEntity.ok(
                ResponseBase.<Map<String, Object>>builder()
                        .data(Map.of("result", result))
                        .message("Validate OTP successfully")
                        .build()
        );
    }

    @PostMapping("/forgot-password/change-password")
    public ResponseEntity<ResponseBase<Void>> changePasswordForgot(@RequestBody UserRequest req) {
        authService.changePasswordForgot(req);

        return ResponseEntity.ok(
                ResponseBase.<Void>builder()
                        .message("Change password successfully")
                        .build()
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseBase<Void>> changePassword(@RequestBody ChangePasswordRequest req) {
        authService.changePassword(req);

        return ResponseEntity.ok(
                ResponseBase.<Void>builder()
                        .message("Change password successfully")
                        .build()
        );
    }
}