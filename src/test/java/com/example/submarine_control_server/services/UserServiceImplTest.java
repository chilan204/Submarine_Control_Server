package com.example.submarine_control_server.services;

import com.example.submarine_control_server.dto.request.UserUpdateRequest;
import com.example.submarine_control_server.dto.response.UserResponse;
import com.example.submarine_control_server.entities.Role;
import com.example.submarine_control_server.entities.User;
import com.example.submarine_control_server.mapper.UserMapper;
import com.example.submarine_control_server.repositories.UserRepository;
import com.example.submarine_control_server.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserServiceImpl service;

    @Test
    void updateWithoutPasswordPreservesExistingHash() {
        User user = existingUser();
        UserUpdateRequest request = request(null);
        Role role = new Role();
        UserResponse response = new UserResponse();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.resolveRole("ADMIN")).thenReturn(role);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(response);

        service.updateUser(1L, request);

        assertEquals("existing-hash", user.getPassword());
        verify(passwordEncoder, never()).encode(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void updateWithPasswordStoresEncodedValue() {
        User user = existingUser();
        UserUpdateRequest request = request("new-password");
        Role role = new Role();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.resolveRole("ADMIN")).thenReturn(role);
        when(passwordEncoder.encode("new-password")).thenReturn("new-hash");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(new UserResponse());

        service.updateUser(1L, request);

        assertEquals("new-hash", user.getPassword());
    }

    private User existingUser() {
        User user = new User();
        user.setId(1L);
        user.setPassword("existing-hash");
        return user;
    }

    private UserUpdateRequest request(String password) {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("admin");
        request.setName("Admin");
        request.setEmail("admin@example.com");
        request.setPhone("0123456789");
        request.setRoleCode("ADMIN");
        request.setPassword(password);
        return request;
    }
}
