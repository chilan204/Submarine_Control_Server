package com.example.submarine_control_server.services.impl;

import com.example.submarine_control_server.dto.response.VoiceCommandDetail;
import com.example.submarine_control_server.dto.response.VoiceCommandResponse;
import com.example.submarine_control_server.entities.User;
import com.example.submarine_control_server.enums.CommandArbitrationStatus;
import com.example.submarine_control_server.repositories.UserRepository;
import com.example.submarine_control_server.services.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class VoiceCommandServiceImpl implements VoiceCommandService {

    private final AIService aiService;
    private final ObjectMapper mapper;
    private final UserRepository userRepository;
    private final UserSessionService userSessionService;
    private final CommandArbitrationService arbitrationService;
    private final CommandAuthorizationService commandAuthorizationService;
    private final AuvCommandService auvCommandService;

    @Override
    public VoiceCommandResponse handleVoiceCommand(MultipartFile file, String language) {
        try (InputStream is = file.getInputStream()) {

            // 1. GET CURRENT USER
            User user = getCurrentUser();
            if (user == null) {
                return null;
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
            VoiceCommandResponse response = mapper.treeToValue(node, VoiceCommandResponse.class);

            // 3. VERIFY SPEAKER
            Double verificationScore = response.getVerificationScore();
            if (verificationScore == null || verificationScore < 0.45) {
                response.setStatus("SPEAKER_VERIFICATION_FAILED");
                return response;
            }

            // 4. EXTRACT COMMAND TEXT
            VoiceCommandDetail command = response.getCommand();
            if (command == null) {
                response.setStatus("INVALID_COMMAND");
                return response;
            }

            String commandText = command.toCommandText();

            // 5. COMMAND ARBITRATION
            CommandArbitrationStatus arbitrationStatus = arbitrationService.processCommand(user, commandText);
            if (arbitrationStatus != CommandArbitrationStatus.EXECUTED) {
                response.setStatus(arbitrationStatus.name());
                response.setRole(user.getRole().getCode());
                return response;
            }

            // 6. ROLE AUTHORIZATION
            boolean allowed = commandAuthorizationService.isAllowed(user.getRole(), command);
            if (!allowed) {
                response.setStatus("ROLE_DENIED");
                response.setRole(user.getRole().getCode());
                return response;
            }

            // 7. EXECUTE COMMAND
            auvCommandService.execute(command);

            // 8. SEND UDP COMMAND (IF APPLICABLE)
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

            response.setStatus(CommandArbitrationStatus.EXECUTED.name());
            response.setRole(user.getRole().getCode());

            // 9. SAVE SESSION / AUDIT
            userSessionService.createFromAIResponse(user, response);

            return response;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
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
