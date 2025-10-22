package com.mt.KickBet.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())           // wyłącza CSRF dla POST/PUT/DELETE
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // wszystkie endpointy dostępne
        return http.build();
    }
}
