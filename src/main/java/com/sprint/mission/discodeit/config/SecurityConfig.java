package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.security.jwt.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.jwt.JwtLogoutHandler;
import com.sprint.mission.discodeit.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
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
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${security.remember-me.key}") private String REMEMBER_KEY;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtLoginSuccessHandler jwtLoginSuccessHandler,
                                           LoginFailureHandler loginFailureHandler,
                                           SessionRegistry sessionRegistry,
                                            JwtLogoutHandler jwtLogoutHandler
//                                           DiscodeitUserDetailsService userDetailsService
    ) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/","/error","/index.html","/favicon.ico","/assets/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/auth/csrf-token").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/users").permitAll()
                        .requestMatchers("/actuator/health","/actuator/info","/swagger-ui/**", "/swagger-ui.html","/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
                .formLogin(login -> login
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(jwtLoginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
//                        .deleteCookies("JSESSIONID","remember-me")
                        .addLogoutHandler(jwtLogoutHandler)
                        .logoutSuccessHandler(
                                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler(new AccessDeniedHandlerImpl())
                )
                .sessionManagement(management -> management
                        .sessionConcurrency(concurrency -> concurrency
                                .maximumSessions(1)
                                .maxSessionsPreventsLogin(false) //rememberMe 설정 시 false 혹은 maximum을 2로 해야 정상 실행
                                .sessionRegistry(sessionRegistry)
                        )
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 정책 : Stateless
/*                ) // cookie의 refresh 토큰이 이걸 대체.
                .rememberMe(remember -> remember
                        .key(REMEMBER_KEY)
                        .rememberMeParameter("remember-me")
                        .rememberMeCookieName("remember-me")
                        .tokenValiditySeconds(60 * 30)
                        .userDetailsService(userDetailsService)
                        .useSecureCookie(false)*/
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ApplicationRunner adminInitializer(AuthService authService){
        return args -> authService.initializeAdmin();
    }

    @Bean
    public RoleHierarchy roleHierarchy(){
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role(Role.ADMIN.name())
                .implies(Role.CHANNEL_MANAGER.name())
                .role(Role.CHANNEL_MANAGER.name())
                .implies(Role.USER.name())
                .build();
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            RoleHierarchy roleHierarchy){
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    // 세션 만료 혹은 제거시 이벤트 발행 && session registry가 세션을 정리할 수 있게 함.
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher(){
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry(){
        return new SessionRegistryImpl();
    }
}
