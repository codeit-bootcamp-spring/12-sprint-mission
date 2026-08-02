package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

  // 서버를 재시작해도 발급된 remember-me 토큰이 유지되도록 키를 고정한다
  @Value("${discodeit.security.remember-me.key}")
  private String rememberMeKey;

  @Value("${discodeit.security.remember-me.validity-seconds}")
  private int rememberMeValiditySeconds;

  // 비밀번호는 평문 저장 없이 BCrypt 해시로만 저장한다 (salt 포함 60자)
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      LoginSuccessHandler loginSuccessHandler,
      LoginFailureHandler loginFailureHandler
  ) throws Exception {
    http
        // CSR 환경이므로 CSRF 토큰을 쿠키로 내려주고, JS가 읽을 수 있도록 HttpOnly는 false
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )
        // 로그인은 UsernamePasswordAuthenticationFilter가 처리한다 (기존 AuthService.login 대체)
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )
        // 로그인 유지: 세션이 만료돼도 remember-me 쿠키로 자동 재인증
        .rememberMe(rememberMe -> rememberMe
            .key(rememberMeKey)
            .rememberMeParameter("remember-me")
            .tokenValiditySeconds(rememberMeValiditySeconds)
        )
        // 로그아웃 흐름은 LogoutFilter가 그대로 처리하고, 처리 URL과 성공 응답만 대체한다
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            // 디폴트(SimpleUrlLogoutSuccessHandler)는 리다이렉트하므로 204만 반환하도록 대체
            .logoutSuccessHandler(
                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
        );

    return http.build();
  }
}
