package com.example.notification;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // needed for WebSocket handshake
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws/**").permitAll() // allow WebSocket handshake
                        .anyRequest().authenticated()         // secure other endpoints
                );

        return http.build();
    }
}
