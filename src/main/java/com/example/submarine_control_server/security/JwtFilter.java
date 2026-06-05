package com.example.submarine_control_server.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    private static final List<String> WHITE_LIST = List.of(
            "/api/auth",
            "/internal/voice-admin",
            "/internal/command-admin",
            "/v3/api-docs",
            "/swagger-ui"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getServletPath();

        if (WHITE_LIST.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String token = null;

        try {

            if (authHeader != null && authHeader.startsWith("Bearer ")) {

                token = authHeader.substring(7);

                try {
                    username = jwtUtil.extractUsername(token);

                } catch (SignatureException e) {
                    logger.warn("Invalid JWT signature");

                } catch (ExpiredJwtException e) {
                    logger.warn("JWT expired");
                }
            }

            if (
                    username != null &&
                            SecurityContextHolder.getContext().getAuthentication() == null
            ) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, userDetails)) {

                    String roleStr = jwtUtil.extractClaim(
                            token,
                            claims -> claims.get("role", String.class)
                    );

                    List<SimpleGrantedAuthority> authorities = List.of();

                    if (roleStr != null && !roleStr.isBlank()) {

                        authorities = List.of(
                                new SimpleGrantedAuthority("ROLE_" + roleStr)
                        );
                    }

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    authorities
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);
                }
            }

        } catch (Exception e) {
            logger.error("JWT filter error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}