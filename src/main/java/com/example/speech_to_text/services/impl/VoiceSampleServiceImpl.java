package com.example.speech_to_text.services.impl;

import com.example.speech_to_text.dto.response.VoiceSampleResponse;
import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.entities.VoiceSample;
import com.example.speech_to_text.repositories.UserRepository;
import com.example.speech_to_text.repositories.VoiceSampleRepository;
import com.example.speech_to_text.services.VoiceSampleService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final String baseDir;

    @Autowired
    public VoiceSampleServiceImpl(
            UserRepository userRepository,
            VoiceSampleRepository voiceSampleRepository,
            @Value("${app.voice.storage:voice_samples}") String baseDir
    ) {
        this.userRepository = userRepository;
        this.voiceSampleRepository = voiceSampleRepository;
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
    public List<VoiceSampleResponse> getAllVoiceSamples() {
        return voiceSampleRepository.findAll()
                .stream()
                .map(v -> new VoiceSampleResponse(
                        v.getUser().getId(),
                        v.getFilePath()
                ))
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
            voiceSampleRepository.findByUserId(userId).ifPresent(old -> {
                try {
                    Files.deleteIfExists(Paths.get(baseDir, old.getFilePath()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                voiceSampleRepository.delete(old);
            });

            String safeName = Paths.get(
                    file.getOriginalFilename() == null ? "audio.wav" : file.getOriginalFilename()
            ).getFileName().toString();

            String fileName = "voice_" + UUID.randomUUID() + "_" + safeName;
            String relativePath = userId + "/" + fileName;

            Path targetPath = Paths.get(baseDir, relativePath);
            Files.createDirectories(targetPath.getParent());

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            VoiceSample voiceSample = VoiceSample.builder()
                    .fileName(fileName)
                    .filePath(relativePath)
                    .duration(extractDuration(targetPath))
                    .active(true)
                    .user(user)
                    .build();

            voiceSampleRepository.save(voiceSample);

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

        try {
            Files.deleteIfExists(Paths.get(baseDir, sample.getFilePath()));
            voiceSampleRepository.delete(sample);
        } catch (IOException e) {
            throw new RuntimeException("Delete failed", e);
        }
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