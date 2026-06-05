package com.example.submarine_control_server.repositories;

import com.example.submarine_control_server.entities.VoiceSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoiceSampleRepository extends JpaRepository<VoiceSample, Long> {
    Optional<VoiceSample> findByUserId(Long userId);
    List<VoiceSample> findByActiveIsTrue();
}