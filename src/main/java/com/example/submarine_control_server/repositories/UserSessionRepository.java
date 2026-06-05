package com.example.submarine_control_server.repositories;

import com.example.submarine_control_server.entities.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    List<UserSession> findByUserIdOrderByIdDesc(Long userId);
}