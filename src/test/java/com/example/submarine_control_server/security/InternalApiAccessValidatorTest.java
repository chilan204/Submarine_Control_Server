package com.example.submarine_control_server.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InternalApiAccessValidatorTest {

    @Test
    void acceptsMatchingServiceToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-AI-Internal-Token")).thenReturn("secret-token");

        InternalApiAccessValidator validator =
                new InternalApiAccessValidator("secret-token");

        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    void rejectsMissingServiceTokenWhenConfigured() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        InternalApiAccessValidator validator =
                new InternalApiAccessValidator("secret-token");

        assertThrows(ResponseStatusException.class, () -> validator.validate(request));
    }
}
