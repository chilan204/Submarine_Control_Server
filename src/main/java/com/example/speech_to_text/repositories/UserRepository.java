package com.example.speech_to_text.repositories;

import com.example.speech_to_text.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
        SELECT u FROM User u
        JOIN FETCH u.role
        WHERE u.username = :username
    """)
    Optional<User> findByUsername(@Param("username") String username);

    Optional<User> findByEmail(String email);
}