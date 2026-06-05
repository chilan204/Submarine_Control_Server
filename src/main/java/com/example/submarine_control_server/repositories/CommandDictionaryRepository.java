package com.example.submarine_control_server.repositories;

import com.example.submarine_control_server.entities.CommandDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandDictionaryRepository extends JpaRepository<CommandDictionary, Long> {
}