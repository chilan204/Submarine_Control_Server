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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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

    @Value("${app.auv.host}")
    private String auvHost;

    @Value("${app.auv.port:5600}")
    private int auvPort;

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
            //    - Offline Vosk transcription with command grammar
            //    - Command Extraction
            String json = aiService.processVoice(is, language);
            if (json == null || json.isBlank()) {
                throw new RuntimeException("Empty AI response");
            }

            JsonNode node = mapper.readTree(json);
            VoiceCommandResponse response = mapper.treeToValue(node, VoiceCommandResponse.class);

            // 3. VERIFY SPEAKER
            Double verificationScore = response.getVerificationScore();
            if (!Boolean.TRUE.equals(response.getVerified())
                    || verificationScore == null
                    || verificationScore < 0.45) {
                response.setStatus("SPEAKER_VERIFICATION_FAILED");
                return response;
            }

            // The voice is a second authorization factor for the current user,
            // not merely any registered speaker.
            if (response.getSpeaker() == null
                    || !response.getSpeaker().equals(String.valueOf(user.getId()))) {
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

            // 5. ROLE AUTHORIZATION (before the command can affect arbitration)
            boolean allowed = commandAuthorizationService.isAllowed(user.getRole(), command);
            if (!allowed) {
                response.setStatus("ROLE_DENIED");
                response.setRole(user.getRole().getCode());
                return response;
            }

            // The currently deployed AUV accepts only forward/backward movement.
            if (!isSupportedHardwareCommand(command)) {
                response.setStatus("INVALID_COMMAND");
                response.setRole(user.getRole().getCode());
                return response;
            }

            // 6. COMMAND ARBITRATION
            CommandArbitrationStatus arbitrationStatus = arbitrationService.processCommand(user, commandText);
            if (arbitrationStatus != CommandArbitrationStatus.EXECUTED) {
                response.setStatus(arbitrationStatus.name());
                response.setRole(user.getRole().getCode());
                return response;
            }

            // 7. DELIVER TO AUV. Never report success when delivery fails.
            try {
                sendUdpCommand(command);
            } catch (Exception e) {
                response.setStatus("DELIVERY_FAILED");
                response.setRole(user.getRole().getCode());
                userSessionService.createFromAIResponse(user, response);
                return response;
            }

            // 8. Record the successfully delivered command locally.
            auvCommandService.execute(command);
            response.setStatus(CommandArbitrationStatus.EXECUTED.name());
            response.setRole(user.getRole().getCode());

            // 9. SAVE SESSION / AUDIT
            userSessionService.createFromAIResponse(user, response);

            return response;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private boolean isSupportedHardwareCommand(VoiceCommandDetail command) {
        if (!"MOVE".equals(command.getAction()) || command.getDirection() == null) {
            return false;
        }
        return "FORWARD".equals(command.getDirection())
                || "BACKWARD".equals(command.getDirection());
    }

    private void sendUdpCommand(VoiceCommandDetail command) throws Exception {
        String payload = mapper.writeValueAsString(Map.of(
                "command", command.getDirection().toLowerCase()
        ));
        byte[] sendData = payload.getBytes(StandardCharsets.UTF_8);
        InetAddress address = InetAddress.getByName(auvHost);

        try (DatagramSocket socket = new DatagramSocket()) {
            DatagramPacket packet = new DatagramPacket(
                    sendData,
                    sendData.length,
                    address,
                    auvPort
            );
            socket.send(packet);
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
