package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.RestAccessDeniedHandler;
import com.sprint.mission.discodeit.security.RestAuthenticationEntryPoint;
import com.sprint.mission.discodeit.security.RestSessionInformationExpiredStrategy;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableMethodSecurity
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

  // 관리자 > 채널 매니저 > 일반 사용자 (상위 권한이 하위 권한을 포함)
  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.withDefaultRolePrefix()
        .role("ADMIN").implies("CHANNEL_MANAGER")
        .role("CHANNEL_MANAGER").implies("USER")
        .build();
  }

  // Method Security(@PreAuthorize)에서도 권한 계층이 적용되도록 ExpressionHandler에 주입
  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }

  // 로그인 중인 사용자의 세션 정보를 관리 (온라인 여부 판단, 세션 강제 만료에 사용)
  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  // HttpSession이 만료/무효화되면 SessionRegistry의 SessionInformation도 함께 정리되도록 이벤트 발행
  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      LoginSuccessHandler loginSuccessHandler,
      LoginFailureHandler loginFailureHandler,
      RestAuthenticationEntryPoint authenticationEntryPoint,
      RestAccessDeniedHandler accessDeniedHandler,
      RestSessionInformationExpiredStrategy sessionExpiredStrategy,
      SessionRegistry sessionRegistry
  ) throws Exception {
    http
        // 동일 계정 동시 로그인 차단: 새로 로그인하면 기존 세션을 만료시킨다
        .sessionManagement(management -> management
            .sessionConcurrency(concurrency -> concurrency
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry)
                .expiredSessionStrategy(sessionExpiredStrategy)
            )
        )
        .authorizeHttpRequests(auth -> auth
            // 인증 없이 접근해야 하는 요청
            .requestMatchers(HttpMethod.GET, "/api/auth/csrf-token").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/logout").permitAll()
            // API가 아닌 요청 (정적 리소스, Swagger, Actuator)
            .requestMatchers("/", "/index.html", "/favicon.ico", "/assets/**", "/error").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()
            .anyRequest().authenticated()
        )
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
        )
        // 리다이렉트 대신 401/403 ErrorResponse를 반환
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        );

    return http.build();
  }
}
