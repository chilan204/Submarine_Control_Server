package com.example.speech_to_text.repositories;

import com.example.speech_to_text.entities.CommandDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandDictionaryRepository extends JpaRepository<CommandDictionary, Long> {
}