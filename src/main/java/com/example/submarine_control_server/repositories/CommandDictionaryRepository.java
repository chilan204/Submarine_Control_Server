package com.example.submarine_control_server.repositories;

import com.example.submarine_control_server.entities.CommandDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandDictionaryRepository extends JpaRepository<CommandDictionary, Long> {
    List<CommandDictionary> findByActiveIsTrue();
}