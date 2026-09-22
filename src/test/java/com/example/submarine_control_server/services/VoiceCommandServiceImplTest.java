package com.example.submarine_control_server.services;

import com.example.submarine_control_server.entities.Role;
import com.example.submarine_control_server.entities.User;
import com.example.submarine_control_server.repositories.UserRepository;
import com.example.submarine_control_server.services.impl.VoiceCommandServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoiceCommandServiceImplTest {

    @Mock private AIService aiService;
    @Spy private ObjectMapper mapper = new ObjectMapper();
    @Mock private UserRepository userRepository;
    @Mock private UserSessionService userSessionService;
    @Mock private CommandArbitrationService arbitrationService;
    @Mock private CommandAuthorizationService commandAuthorizationService;
    @Mock private AuvCommandService auvCommandService;

    @InjectMocks private VoiceCommandServiceImpl service;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void rejectsUnauthorizedCommandBeforeArbitration() throws Exception {
        Role role = new Role();
        role.setCode("OFFICER_1");
        role.setPriority(1);

        User user = new User();
        user.setId(7L);
        user.setUsername("officer");
        user.setRole(role);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("officer", null)
        );
        when(userRepository.findByUsername("officer")).thenReturn(Optional.of(user));
        when(aiService.processVoice(any(), any())).thenReturn("""
                {
                  "speaker_id": "7",
                  "speaker_score": 0.9,
                  "verification_score": 0.9,
                  "verified": true,
                  "text": "tiến",
                  "command": {"action": "MOVE", "direction": "FORWARD"}
                }
                """);
        when(commandAuthorizationService.isAllowed(any(), any())).thenReturn(false);

        MockMultipartFile audio = new MockMultipartFile(
                "file", "command.wav", "audio/wav", new byte[]{1}
        );

        var response = service.handleVoiceCommand(audio, "vi");

        assertEquals("ROLE_DENIED", response.getStatus());
        verify(arbitrationService, never()).processCommand(any(), any());
        verify(auvCommandService, never()).execute(any());
    }
}
