package com.example.submarine_control_server.services.impl;

import com.example.submarine_control_server.dto.response.VoiceSampleResponse;
import com.example.submarine_control_server.entities.User;
import com.example.submarine_control_server.entities.VoiceSample;
import com.example.submarine_control_server.mapper.VoiceSampleMapper;
import com.example.submarine_control_server.repositories.UserRepository;
import com.example.submarine_control_server.repositories.VoiceSampleRepository;
import com.example.submarine_control_server.services.VoiceSampleService;
import com.example.submarine_control_server.services.AIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class VoiceSampleServiceImpl implements VoiceSampleService {

    private final UserRepository userRepository;
    private final VoiceSampleRepository voiceSampleRepository;
    private final VoiceSampleMapper  voiceSampleMapper;
    private final AIService aiService;
    private final String baseDir;

    public VoiceSampleServiceImpl(
            UserRepository userRepository,
            VoiceSampleRepository voiceSampleRepository,
            VoiceSampleMapper voiceSampleMapper,
            AIService aiService,
            @Value("${app.voice.storage:voice_samples}") String baseDir
    ) {
        this.userRepository = userRepository;
        this.voiceSampleRepository = voiceSampleRepository;
        this.voiceSampleMapper = voiceSampleMapper;
        this.aiService = aiService;
        this.baseDir = baseDir;
    }

    @PostConstruct
    private void initBaseDir() {
        try {
            Files.createDirectories(Paths.get(baseDir));
        } catch (IOException e) {
            throw new RuntimeException("Cannot init voice storage dir", e);
        }
    }

    @Override
    public List<VoiceSampleResponse> getActiveVoiceSamples() {
        return voiceSampleRepository.findByActiveIsTrue()
                .stream()
                .map(voiceSampleMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<VoiceSampleResponse> getAllVoiceSamples() {
        return voiceSampleRepository.findAll()
                .stream()
                .map(voiceSampleMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void saveVoiceSample(Long userId, MultipartFile file) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        try {
            // ================= GET EXISTING SAMPLE =================
            VoiceSample existing = voiceSampleRepository.findByUserId(userId)
                    .orElse(null);

            String oldFilePath = null;

            if (existing != null) {
                oldFilePath = existing.getFilePath();
            }

            // ================= SAVE NEW FILE =================
            String safeName = Paths.get(
                    file.getOriginalFilename() == null
                            ? "audio.wav"
                            : file.getOriginalFilename()
            ).getFileName().toString();

            String fileName = "voice_" + UUID.randomUUID() + "_" + safeName;
            String relativePath = userId + "/" + fileName;

            Path targetPath = Paths.get(baseDir, relativePath);
            Files.createDirectories(targetPath.getParent());

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // ================= EXTRACT EMBEDDING =================
            String embeddingString = null;
            try {
                String aiResponse = aiService.extractEmbedding(file.getInputStream());
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(aiResponse);
                JsonNode embeddingNode = rootNode.get("embedding");
                if (embeddingNode != null && embeddingNode.isArray()) {
                    embeddingString = embeddingNode.toString();
                }
            } catch (Exception e) {
                System.out.println("Warning: Cannot extract speaker embedding: " + e.getMessage());
            }

            // ================= UPSERT ENTITY =================
            VoiceSample voiceSample = (existing != null)
                    ? existing
                    : new VoiceSample();

            voiceSample.setUser(user);
            voiceSample.setFileName(fileName);
            voiceSample.setFilePath(relativePath);
            voiceSample.setDuration(extractDuration(targetPath));
            voiceSample.setActive(true);
            voiceSample.setEmbedding(embeddingString);

            voiceSampleRepository.save(voiceSample);

            // ================= DELETE OLD FILE (SAFE AFTER SAVE) =================
            if (oldFilePath != null) {
                try {
                    Files.deleteIfExists(Paths.get(baseDir, oldFilePath));
                } catch (IOException e) {
                    System.out.println("Cannot delete old file: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error saving voice sample", e);
        }
    }

    @Override
    public VoiceSample getVoiceSample(Long userId) {
        return voiceSampleRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No voice sample"));
    }

    @Override
    public void deleteVoiceSample(Long userId) {

        VoiceSample sample = voiceSampleRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        User user = sample.getUser();
        if (user != null) {
            user.setVoiceSample(null);
        }

        try {
            Files.deleteIfExists(Paths.get(baseDir, sample.getFilePath()));
            voiceSampleRepository.delete(sample);
        } catch (IOException e) {
            throw new RuntimeException("Delete failed", e);
        }
    }

    @Override
    public void toggleActive(Long userId) {
        VoiceSample sample = voiceSampleRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Not found"));
        
        sample.setActive(sample.getActive() == null || !sample.getActive());
        voiceSampleRepository.save(sample);
    }

    private Double extractDuration(Path audioPath) {
        try (AudioInputStream audioInputStream =
                     AudioSystem.getAudioInputStream(audioPath.toFile())) {

            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();

            return (double) (frames / format.getFrameRate());

        } catch (Exception e) {
            return null;
        }
    }
}