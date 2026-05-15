package com.example.speech_to_text.repositories;

import com.example.speech_to_text.entities.VoiceSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoiceSampleRepository extends JpaRepository<VoiceSample, Long> {
    Optional<VoiceSample> findByUserId(Long userId);
}