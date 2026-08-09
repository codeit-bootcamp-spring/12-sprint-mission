package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.RestAccessDeniedHandler;
import com.sprint.mission.discodeit.security.RestAuthenticationEntryPoint;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.security.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.security.jwt.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.jwt.JwtLogoutHandler;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

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

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      JwtLoginSuccessHandler jwtLoginSuccessHandler,
      LoginFailureHandler loginFailureHandler,
      RestAuthenticationEntryPoint authenticationEntryPoint,
      RestAccessDeniedHandler accessDeniedHandler,
      JwtLogoutHandler jwtLogoutHandler,
      JwtTokenProvider jwtTokenProvider,
      JwtRegistry jwtRegistry
  ) throws Exception {
    http
        // 인증 상태를 토큰으로만 판단하므로 서버는 세션을 만들지도, 참조하지도 않는다
        .sessionManagement(management -> management
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(auth -> auth
            // 인증 없이 접근해야 하는 요청
            .requestMatchers(HttpMethod.GET, "/api/auth/csrf-token").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/logout").permitAll()
            // 엑세스 토큰이 없거나 만료된 상태에서 호출되는 API
            .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
            // API가 아닌 요청 (정적 리소스, Swagger, Actuator)
            .requestMatchers("/", "/index.html", "/favicon.ico", "/assets/**", "/error").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            // 로드밸런서 헬스체크는 인증 없이 통과해야 한다
            .requestMatchers("/actuator/health").permitAll()
            // 나머지 actuator는 관리자만. loggers는 인증 없이 열어두면 로그 레벨을 바꿀 수 있고,
            // info는 management.info.env로 DB 접속 정보까지 노출될 수 있다
            .requestMatchers("/actuator/**").hasRole("ADMIN")
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
            .successHandler(jwtLoginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )
        // 로그아웃 흐름은 LogoutFilter가 그대로 처리하고, 처리 URL과 토큰 무효화, 성공 응답만 대체한다
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .addLogoutHandler(jwtLogoutHandler)
            // 디폴트(SimpleUrlLogoutSuccessHandler)는 리다이렉트하므로 204만 반환하도록 대체
            .logoutSuccessHandler(
                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
        )
        // 리다이렉트 대신 401/403 ErrorResponse를 반환
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        )
        // 로그인 요청을 처리하는 UsernamePasswordAuthenticationFilter보다 앞에 두어,
        // 이미 토큰을 가진 요청은 로그인 절차를 거치지 않고 인증되도록 한다.
        // @Component로 등록하지 않는 이유는 Boot가 서블릿 필터 체인에도 자동 등록해 두 번 실행되기 때문이다.
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, jwtRegistry),
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
