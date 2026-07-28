package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        // 디폴트(HttpSessionCsrfTokenRepository) 대신 쿠키 기반 저장소 사용
                        //    - 클라이언트 JS가 쿠키(XSRF-TOKEN)를 읽어야 하므로 HttpOnly=false
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        // 디폴트(XorCsrfTokenRequestAttributeHandler)
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/csrf-token").permitAll()
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}






