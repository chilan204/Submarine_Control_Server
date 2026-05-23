package com.example.speech_to_text.services.impl;

import com.example.speech_to_text.dto.response.UserSessionResponse;
import com.example.speech_to_text.dto.response.VoiceCommandResponse;
import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.entities.UserSession;
import com.example.speech_to_text.enums.CommandArbitrationStatus;
import com.example.speech_to_text.mapper.UserSessionMapper;
import com.example.speech_to_text.repositories.UserSessionRepository;
import com.example.speech_to_text.services.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository userSessionRepository;
    private final UserSessionMapper userSessionMapper;

    @Override
    public List<UserSessionResponse> getAllUserSession() {
        return userSessionRepository.findAll()
                .stream()
                .map(userSessionMapper::toResponseDTO)
                .toList();
    }

    @Override
    public UserSessionResponse getUserSessionById(Long id) {
        return userSessionRepository.findById(id)
                .map(userSessionMapper::toResponseDTO)
                .orElse(null);
    }

    @Override
    public List<UserSessionResponse> getUserSessionByUserId(Long userId) {
        return userSessionRepository.findByUserIdOrderByIdDesc(userId)
                .stream()
                .map(userSessionMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void createFromAIResponse(User user, VoiceCommandResponse response) {
        UserSession session = UserSession.builder()
                .user(user)
                .transcript(response.getText())
                .action(response.getCommand() != null ? response.getCommand().getAction() : null)
                .direction(response.getCommand() != null ? response.getCommand().getDirection() : null)
                .value(response.getCommand() != null ? response.getCommand().getValue() : null)
                .speaker(response.getSpeaker())
                .speakerScore(response.getSpeakerScore())
                .verificationScore(response.getVerificationScore())
                .role(response.getRole())
                .commandStatus(response.getStatus())
                .executed(
                        CommandArbitrationStatus.EXECUTED.name()
                                .equals(response.getStatus()))
                .build();
        userSessionRepository.save(session);
    }

    @Override
    public void deleteUserSession(Long id) {
        userSessionRepository.deleteById(id);
    }
}