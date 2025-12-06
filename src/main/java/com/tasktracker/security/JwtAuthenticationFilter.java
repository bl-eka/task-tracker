package com.tasktracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("=== JWT FILTER CALLED ===");
        log.info("Request URI: {}", request.getRequestURI());
        log.info("Request Method: {}", request.getMethod());

        final String authHeader = request.getHeader("Authorization");
        log.info("Authorization Header: {}", authHeader);

        // Пропускаем, если нет Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("JwtAuthenticationFilter: No valid Authorization header, skipping JWT processing");
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        log.info("JwtAuthenticationFilter: JWT token extracted (first 30 chars): {}",
                jwt.substring(0, Math.min(jwt.length(), 30)) + "...");

        try {
            String userEmail = jwtService.extractEmail(jwt);
            log.info("JwtAuthenticationFilter: Extracted email from JWT: {}", userEmail);

            // Если email извлечен и пользователь еще не аутентифицирован
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("JwtAuthenticationFilter: Loading user details for email: {}", userEmail);

                // Загружаем пользователя из базы
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                log.info("JwtAuthenticationFilter: User details loaded: {}", userDetails.getUsername());
                log.info("JwtAuthenticationFilter: User authorities: {}", userDetails.getAuthorities());

                // Проверяем валидность токена
                boolean isValid = jwtService.validateToken(jwt);
                log.info("JwtAuthenticationFilter: Token validation result: {}", isValid);

                if (isValid) {
                    log.info("JwtAuthenticationFilter: Token is valid, creating authentication token");

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("JwtAuthenticationFilter: Authentication set in SecurityContext");

                    // Проверим, что аутентификация установлена
                    var auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null && auth.isAuthenticated()) {
                        log.info("JwtAuthenticationFilter: User successfully authenticated: {}", auth.getName());
                    }
                } else {
                    log.warn("JwtAuthenticationFilter: Token is invalid for user: {}", userEmail);
                }
            } else {
                if (userEmail == null) {
                    log.warn("JwtAuthenticationFilter: Could not extract email from JWT");
                }
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    log.info("JwtAuthenticationFilter: User already authenticated");
                }
            }
        } catch (Exception e) {
            log.error("JwtAuthenticationFilter: Error processing JWT token: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}