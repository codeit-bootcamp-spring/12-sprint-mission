package com.sprint.mission.discodeit.integration;

import static com.sprint.mission.discodeit.support.SecurityTestSupport.asUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  private String signUp(String username, String email) throws Exception {
    MockMultipartFile part = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new UserCreateRequest(username, email, "password123!")));
    MvcResult result = mockMvc.perform(multipart("/api/users").file(part).with(csrf()))
        .andExpect(status().isCreated())
        .andReturn();
    return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
  }

  @Test
  @DisplayName("로그인 → 현재 사용자 조회 → 로그아웃 흐름")
  void login_me_logout() throws Exception {
    signUp("loginuser", "loginuser@email.com");

    MvcResult loginResult = mockMvc.perform(
            formLogin("/api/auth/login").user("loginuser").password("password123!"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("loginuser"))
        .andReturn();
    MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

    // 세션 ID만으로 현재 사용자 정보를 조회할 수 있어야 한다
    mockMvc.perform(get("/api/auth/me").session(session))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("loginuser"))
        .andExpect(jsonPath("$.role").value("USER"))
        // 로그인 중이므로 SessionRegistry 기준 online = true
        .andExpect(jsonPath("$.online").value(true));

    mockMvc.perform(post("/api/auth/logout").session(session).with(csrf()))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("비밀번호가 틀리면 401 ErrorResponse 반환")
  void login_wrongPassword_returns401() throws Exception {
    signUp("failuser", "failuser@email.com");

    mockMvc.perform(formLogin("/api/auth/login").user("failuser").password("wrong-password"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
  }

  @Test
  @DisplayName("인증되지 않은 요청은 401 반환")
  void unauthenticatedRequest_returns401() throws Exception {
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  @DisplayName("CSRF 토큰이 없으면 403 반환")
  void requestWithoutCsrfToken_returns403() throws Exception {
    MockMultipartFile part = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new UserCreateRequest("csrfuser", "csrfuser@email.com", "password123!")));

    mockMvc.perform(multipart("/api/users").file(part))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("PUT /api/auth/role - ADMIN은 권한 수정 성공")
  void updateRole_asAdmin_success() throws Exception {
    String targetId = signUp("roletarget", "roletarget@email.com");
    UserRoleUpdateRequest request =
        new UserRoleUpdateRequest(UUID.fromString(targetId), Role.CHANNEL_MANAGER);

    mockMvc.perform(put("/api/auth/role")
            .with(asUser(UUID.randomUUID(), "admin", Role.ADMIN))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.role").value("CHANNEL_MANAGER"));
  }

  @Test
  @DisplayName("PUT /api/auth/role - USER 권한으로는 403 반환")
  void updateRole_asUser_returns403() throws Exception {
    String targetId = signUp("roletarget2", "roletarget2@email.com");
    UserRoleUpdateRequest request =
        new UserRoleUpdateRequest(UUID.fromString(targetId), Role.ADMIN);

    mockMvc.perform(put("/api/auth/role")
            .with(asUser(UUID.randomUUID(), "normal", Role.USER))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
  }

  @Test
  @DisplayName("GET /api/auth/csrf-token - 인증 없이 토큰 발급 가능")
  void getCsrfToken_withoutAuthentication() throws Exception {
    mockMvc.perform(get("/api/auth/csrf-token"))
        .andExpect(status().is(203));
  }
}
