package com.tasktracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Отключаем CSRF для REST API
                .csrf(AbstractHttpConfigurer::disable)

                // Настраиваем доступ
                .authorizeHttpRequests(auth -> auth
                        // Публичные эндпоинты (без авторизации)
                        .requestMatchers(
                                "/api/auth/**",           // регистрация/логин
                                "/api/swagger-ui.html",   // Swagger UI
                                "/api/swagger-ui/**",     // Swagger ресурсы
                                "/api/api-docs/**",       // OpenAPI docs
                                "/api/h2-console/**",     // H2 консоль (если будет)
                                "/v3/api-docs/**"         // OpenAPI JSON
                        ).permitAll()

                        // Все остальные запросы требуют авторизации
                        .anyRequest().authenticated()
                )

                // Отключаем форму логина Spring Security
                .formLogin(form -> form.disable())

                // Отключаем базовую HTTP аутентификацию
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}