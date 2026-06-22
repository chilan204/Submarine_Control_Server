package com.example.submarine_control_server.controllers;

import com.example.submarine_control_server.dto.common.response.ResponseBase;
import com.example.submarine_control_server.dto.response.VoiceCommandDetail;
import com.example.submarine_control_server.dto.response.VoiceCommandResponse;
import com.example.submarine_control_server.entities.User;
import com.example.submarine_control_server.enums.CommandArbitrationStatus;
import com.example.submarine_control_server.repositories.UserRepository;
import com.example.submarine_control_server.services.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/voice-command")
public class VoiceCommandController {

    private final AIService aiService;
    private final ObjectMapper mapper;
    private final UserRepository userRepository;
    private final UserSessionService userSessionService;
    private final CommandArbitrationService arbitrationService;
    private final CommandAuthorizationService commandAuthorizationService;
    private final DroneCommandService droneCommandService;

    @PostMapping
    public ResponseEntity<ResponseBase<VoiceCommandResponse>> handle(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "language", required = false) String language
    ) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseBase.<VoiceCommandResponse>builder()
                            .message("Invalid file")
                            .build()
            );
        }

        try (InputStream is = file.getInputStream()) {

            // 1. GET CURRENT USER

            User user = getCurrentUser();

            if (user == null) {
                return ResponseEntity.status(401).body(
                        ResponseBase.<VoiceCommandResponse>builder()
                                .message("User not found")
                                .build()
                );
            }

            // 2. AI PROCESS
            //    - Speaker Identification
            //    - Speaker Verification
            //    - Whisper Transcription
            //    - Command Extraction

            String json = aiService.processVoice(is, language);

            if (json == null || json.isBlank()) {
                throw new RuntimeException("Empty AI response");
            }

            JsonNode node = mapper.readTree(json);

            VoiceCommandResponse response =
                    mapper.treeToValue(
                            node,
                            VoiceCommandResponse.class
                    );

            // 3. VERIFY SPEAKER

            Double verificationScore =
                    response.getVerificationScore();

            if (verificationScore == null
                    || verificationScore < 0.75) {

                response.setStatus("SPEAKER_VERIFICATION_FAILED");

                return ResponseEntity.status(403).body(
                        ResponseBase.<VoiceCommandResponse>builder()
                                .data(response)
                                .message("Speaker verification failed")
                                .build()
                );
            }

            // 4. EXTRACT COMMAND TEXT

            VoiceCommandDetail command =
                    response.getCommand();

            if (command == null) {

                response.setStatus("INVALID_COMMAND");

                return ResponseEntity.badRequest().body(
                        ResponseBase.<VoiceCommandResponse>builder()
                                .data(response)
                                .message("Cannot extract command")
                                .build()
                );
            }

            String commandText =
                    command.toCommandText();

            // 5. COMMAND ARBITRATION

            CommandArbitrationStatus arbitrationStatus =
                    arbitrationService.processCommand(
                            user,
                            commandText
                    );

            if (arbitrationStatus
                    != CommandArbitrationStatus.EXECUTED) {

                response.setStatus(
                        arbitrationStatus.name()
                );

                response.setRole(
                        user.getRole().getCode()
                );

                return ResponseEntity.status(403).body(
                        ResponseBase.<VoiceCommandResponse>builder()
                                .data(response)
                                .message("Rejected by arbitration")
                                .build()
                );
            }

            // 6. ROLE AUTHORIZATION

            boolean allowed =
                    commandAuthorizationService
                            .isAllowed(
                                    user.getRole(),
                                    command
                            );

            if (!allowed) {

                response.setStatus("ROLE_DENIED");

                response.setRole(
                        user.getRole().getCode()
                );

                return ResponseEntity.status(403).body(
                        ResponseBase.<VoiceCommandResponse>builder()
                                .data(response)
                                .message("Role permission denied")
                                .build()
                );
            }

            // 7. EXECUTE COMMAND

            droneCommandService.execute(command);

            // 8. FINAL RESPONSE

            try (java.net.DatagramSocket socket = new java.net.DatagramSocket()) {
                String direction = command.getDirection() != null ? command.getDirection() : "";
                
                String payload = "{\"command\": \"" + direction.toLowerCase() + "\"}";
                byte[] sendData = payload.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                java.net.InetAddress address = java.net.InetAddress.getByName("100.112.130.80");
                java.net.DatagramPacket packet = new java.net.DatagramPacket(sendData, sendData.length, address, 5600);
                socket.send(packet);
            } catch (Exception e) {
                System.err.println("Lỗi khi gửi UDP: " + e.getMessage());
            }

            response.setStatus(
                    CommandArbitrationStatus.EXECUTED.name()
            );

            response.setRole(
                    user.getRole().getCode()
            );

            // 9. SAVE SESSION / AUDIT

            userSessionService.createFromAIResponse(
                    user,
                    response
            );

            return ResponseEntity.ok(
                    ResponseBase.<VoiceCommandResponse>builder()
                            .data(response)
                            .message("Success")
                            .build()
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(
                    ResponseBase.<VoiceCommandResponse>builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    private User getCurrentUser() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository
                .findByUsername(username)
                .orElse(null);
    }
}