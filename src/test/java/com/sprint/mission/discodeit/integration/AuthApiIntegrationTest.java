package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.test.context.support.WithMockUser;
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
            .andExpect(jsonPath("$.userDto.id", notNullValue()))
            .andExpect(jsonPath("$.userDto.username", is("loginuser")))
            .andExpect(jsonPath("$.userDto.email", is("login@example.com")))
            .andExpect(jsonPath("$.userDto.role", is("USER")))
            .andExpect(jsonPath("$.accessToken", notNullValue()))
            .andReturn();

        assertThat(loginResult.getResponse().getCookie("REFRESH_TOKEN")).isNotNull();
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
    @DisplayName("리프레시 토큰을 회전하며 액세스 토큰을 재발급한다")
    void refresh_RotatesRefreshToken() throws Exception {
        userService.create(
            new UserCreateRequest("refreshuser", "refresh@example.com", "Password1!"),
            Optional.empty()
        );

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .param("username", "refreshuser")
                .param("password", "Password1!"))
            .andExpect(status().isOk())
            .andReturn();
        Cookie refreshToken = loginResult.getResponse().getCookie("REFRESH_TOKEN");

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
                .with(csrf())
                .cookie(refreshToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userDto.username", is("refreshuser")))
            .andExpect(jsonPath("$.accessToken", notNullValue()))
            .andReturn();

        Cookie rotated = refreshResult.getResponse().getCookie("REFRESH_TOKEN");
        assertThat(rotated).isNotNull();
        assertThat(rotated.getValue()).isNotEqualTo(refreshToken.getValue());

        mockMvc.perform(post("/api/auth/refresh")
                .with(csrf())
                .cookie(refreshToken))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status", is(401)));
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
