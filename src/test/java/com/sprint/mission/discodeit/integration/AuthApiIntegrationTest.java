package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Test
    @DisplayName("로그인 API 통합 테스트 - 성공")
    void login_Success() throws Exception {
        // Given
        // 테스트 사용자 생성
        UserCreateRequest userRequest = new UserCreateRequest(
            "loginuser",
            "login@example.com",
            "Password1!"
        );
        
        userService.create(userRequest, Optional.empty());
        
        // When & Then
        MvcResult loginResult = mockMvc.perform(multipart("/api/auth/login")
                .with(csrf())
                .param("username", "loginuser")
                .param("password", "Password1!"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.username", is("loginuser")))
            .andExpect(jsonPath("$.email", is("login@example.com")))
            .andExpect(jsonPath("$.role", is("USER")))
            .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        mockMvc.perform(get("/api/auth/me").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username", is("loginuser")))
            .andExpect(jsonPath("$.email", is("login@example.com")));

        mockMvc.perform(get("/api/users").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.username == 'loginuser')].online", hasItem(true)));

        mockMvc.perform(post("/api/auth/logout").session(session).with(csrf()))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("로그인 API 통합 테스트 - 실패 (존재하지 않는 사용자)")
    void login_Failure_UserNotFound() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .param("username", "nonexistentuser")
                .param("password", "Password1!"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("사용자 권한 수정 API 통합 테스트")
    void updateRole_Success() throws Exception {
        UserDto user = userService.create(
            new UserCreateRequest("roleuser", "role@example.com", "Password1!"),
            Optional.empty()
        );

        mockMvc.perform(put("/api/auth/role")
                .with(csrf())
                .contentType("application/json")
                .content("""
                    {"userId":"%s","newRole":"ADMIN"}
                    """.formatted(user.id())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(user.id().toString())))
            .andExpect(jsonPath("$.role", is(Role.ADMIN.name())));
    }

    @Test
    @DisplayName("인증되지 않은 API 요청은 401을 반환한다")
    void protectedApi_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status", is(401)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("일반 사용자의 권한 수정 요청은 403을 반환한다")
    void updateRole_Forbidden() throws Exception {
        UserDto user = userService.create(
            new UserCreateRequest("normaluser", "normal@example.com", "Password1!"),
            Optional.empty()
        );

        mockMvc.perform(put("/api/auth/role")
                .with(csrf())
                .contentType("application/json")
                .content("""
                    {"userId":"%s","newRole":"ADMIN"}
                    """.formatted(user.id())))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status", is(403)));
    }

    @Test
    @DisplayName("동일한 계정은 동시에 로그인할 수 없다")
    void concurrentLogin_Prevented() throws Exception {
        userService.create(
            new UserCreateRequest("singleuser", "single@example.com", "Password1!"),
            Optional.empty()
        );

        MvcResult firstLogin = mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .param("username", "singleuser")
                .param("password", "Password1!"))
            .andExpect(status().isOk())
            .andReturn();
        MockHttpSession firstSession =
            (MockHttpSession) firstLogin.getRequest().getSession(false);

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .param("username", "singleuser")
                .param("password", "Password1!"))
            .andExpect(status().isOk());

        assertThat(sessionRegistry.getSessionInformation(firstSession.getId()).isExpired()).isTrue();
    }

    @Test
    @DisplayName("remember-me 쿠키로 세션 없이 인증을 복구한다")
    void rememberMe_RestoresAuthentication() throws Exception {
        userService.create(
            new UserCreateRequest("rememberuser", "remember@example.com", "Password1!"),
            Optional.empty()
        );

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .param("username", "rememberuser")
                .param("password", "Password1!")
                .param("remember-me", "true"))
            .andExpect(status().isOk())
            .andReturn();
        Cookie rememberMe = loginResult.getResponse().getCookie("remember-me");

        assertThat(rememberMe).isNotNull();
        mockMvc.perform(get("/api/auth/me").cookie(rememberMe))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username", is("rememberuser")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("권한 변경 시 로그인 세션을 만료한다")
    void updateRole_ExpiresSession() throws Exception {
        UserDto user = userService.create(
            new UserCreateRequest("sessionuser", "session@example.com", "Password1!"),
            Optional.empty()
        );

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .param("username", "sessionuser")
                .param("password", "Password1!"))
            .andExpect(status().isOk())
            .andReturn();
        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        mockMvc.perform(put("/api/auth/role")
                .with(csrf())
                .contentType("application/json")
                .content("""
                    {"userId":"%s","newRole":"CHANNEL_MANAGER"}
                    """.formatted(user.id())))
            .andExpect(status().isOk());

        assertThat(sessionRegistry.getSessionInformation(session.getId()).isExpired()).isTrue();
    }

    @Test
    @DisplayName("로그인 API 통합 테스트 - 실패 (잘못된 비밀번호)")
    void login_Failure_InvalidCredentials() throws Exception {
        // Given
        // 테스트 사용자 생성
        UserCreateRequest userRequest = new UserCreateRequest(
            "loginuser2",
            "login2@example.com",
            "Password1!"
        );
        
        userService.create(userRequest, Optional.empty());
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .param("username", "loginuser2")
                .param("password", "WrongPassword1!"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 API 통합 테스트 - 실패 (유효하지 않은 요청)")
    void login_Failure_InvalidRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .param("username", "")
                .param("password", ""))
            .andExpect(status().isUnauthorized());
    }
} 
