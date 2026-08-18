package com.sprint.mission.discodeit.integration;

import static com.sprint.mission.discodeit.support.SecurityTestSupport.asUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

// 유예 창을 끄면 직전 리프레시 토큰의 재등장이 항상 재사용으로 판정되어 검증이 결정적이다.
// 유예 창이 동작하는 경로는 TokenLifecycleIntegrationTest에서 따로 확인한다.
@SpringBootTest(properties = "discodeit.security.jwt.refresh-grace-seconds=0")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  private record Tokens(String userId, String accessToken, String refreshToken) {

  }

  private String signUp(String username, String email) throws Exception {
    MockMultipartFile part = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new UserCreateRequest(username, email, "password123!")));
    MvcResult result = mockMvc.perform(multipart("/api/users").file(part).with(csrf()))
        .andExpect(status().isCreated())
        .andReturn();
    return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
  }

  private Tokens login(String username) throws Exception {
    MvcResult result = mockMvc.perform(
            formLogin("/api/auth/login").user(username).password("password123!"))
        .andExpect(status().isOk())
        .andReturn();
    JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
    Cookie cookie = result.getResponse().getCookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME);
    return new Tokens(
        body.get("userDto").get("id").asText(),
        body.get("accessToken").asText(),
        cookie == null ? null : cookie.getValue());
  }

  private String bearer(String accessToken) {
    return "Bearer " + accessToken;
  }

  @Test
  @DisplayName("로그인하면 엑세스 토큰은 Body로, 리프레시 토큰은 쿠키로 발급된다")
  void login_issuesTokenPair() throws Exception {
    signUp("loginuser", "loginuser@email.com");

    MvcResult result = mockMvc.perform(
            formLogin("/api/auth/login").user("loginuser").password("password123!"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userDto.username").value("loginuser"))
        .andExpect(jsonPath("$.userDto.role").value("USER"))
        // 레지스트리에 등록된 뒤 계산되므로 online이 true여야 한다
        .andExpect(jsonPath("$.userDto.online").value(true))
        .andExpect(jsonPath("$.accessToken").isNotEmpty())
        .andReturn();

    Cookie refreshToken = result.getResponse()
        .getCookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME);
    assertThat(refreshToken).isNotNull();
    assertThat(refreshToken.getValue()).isNotBlank();
    // XSS로 탈취되지 않도록 JS에서 읽을 수 없어야 한다
    assertThat(refreshToken.isHttpOnly()).isTrue();
  }

  @Test
  @DisplayName("엑세스 토큰이 있으면 인증이 필요한 요청에 접근할 수 있다")
  void accessToken_authenticatesRequest() throws Exception {
    signUp("bearer", "bearer@email.com");
    Tokens tokens = login("bearer");

    mockMvc.perform(get("/api/users").header(HttpHeaders.AUTHORIZATION, bearer(tokens.accessToken())))
        .andExpect(status().isOk());

    // 토큰 없이 같은 요청을 보내면 인증되지 않는다
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("리프레시 토큰으로 재발급하면 토큰 쌍이 모두 교체된다")
  void refresh_rotatesTokenPair() throws Exception {
    signUp("refresher", "refresher@email.com");
    Tokens tokens = login("refresher");

    MvcResult result = mockMvc.perform(post("/api/auth/refresh")
            .cookie(new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, tokens.refreshToken()))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userDto.username").value("refresher"))
        .andExpect(jsonPath("$.accessToken").isNotEmpty())
        .andReturn();

    JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
    String newAccessToken = body.get("accessToken").asText();
    String newRefreshToken = result.getResponse()
        .getCookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME).getValue();

    assertThat(newAccessToken).isNotEqualTo(tokens.accessToken());
    assertThat(newRefreshToken).isNotEqualTo(tokens.refreshToken());

    // 새 엑세스 토큰으로 인증되고, 기존 엑세스 토큰은 무효화된다
    mockMvc.perform(get("/api/users").header(HttpHeaders.AUTHORIZATION, bearer(newAccessToken)))
        .andExpect(status().isOk());
    mockMvc.perform(
            get("/api/users").header(HttpHeaders.AUTHORIZATION, bearer(tokens.accessToken())))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("이미 사용한 리프레시 토큰이 다시 들어오면 해당 사용자의 토큰을 모두 무효화한다")
  void refresh_withReusedToken_invalidatesEverySession() throws Exception {
    signUp("reuser", "reuser@email.com");
    Tokens tokens = login("reuser");

    MvcResult first = mockMvc.perform(post("/api/auth/refresh")
            .cookie(new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, tokens.refreshToken()))
            .with(csrf()))
        .andExpect(status().isOk())
        .andReturn();
    String rotatedAccessToken =
        objectMapper.readTree(first.getResponse().getContentAsString()).get("accessToken").asText();

    // 탈취 시나리오: 공격자가 먼저 재발급받아 간 뒤 원래 사용자가 옛 토큰으로 재발급을 시도한다
    mockMvc.perform(post("/api/auth/refresh")
            .cookie(new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, tokens.refreshToken()))
            .with(csrf()))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("INVALID_REFRESH_TOKEN"));

    // 401만 내면 공격자 토큰이 살아남으므로, 로테이션으로 발급된 토큰까지 함께 죽어야 한다
    mockMvc.perform(
            get("/api/users").header(HttpHeaders.AUTHORIZATION, bearer(rotatedAccessToken)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("리프레시 토큰 쿠키가 없으면 401 반환")
  void refresh_withoutCookie_returns401() throws Exception {
    mockMvc.perform(post("/api/auth/refresh").with(csrf()))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("INVALID_REFRESH_TOKEN"));
  }

  @Test
  @DisplayName("로그아웃하면 엑세스 토큰과 리프레시 토큰이 모두 무효화된다")
  void logout_invalidatesTokens() throws Exception {
    signUp("logoutuser", "logoutuser@email.com");
    Tokens tokens = login("logoutuser");

    // 프론트엔드는 Authorization 헤더 없이 로그아웃을 호출하므로 쿠키만으로 처리되어야 한다
    mockMvc.perform(post("/api/auth/logout")
            .cookie(new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, tokens.refreshToken()))
            .with(csrf()))
        .andExpect(status().isNoContent());

    mockMvc.perform(
            get("/api/users").header(HttpHeaders.AUTHORIZATION, bearer(tokens.accessToken())))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(post("/api/auth/refresh")
            .cookie(new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, tokens.refreshToken()))
            .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("같은 계정으로 다시 로그인하면 기존 로그인이 무효화된다")
  void secondLogin_invalidatesFirstSession() throws Exception {
    signUp("duplicate", "duplicate@email.com");
    Tokens first = login("duplicate");
    Tokens second = login("duplicate");

    mockMvc.perform(get("/api/users").header(HttpHeaders.AUTHORIZATION, bearer(second.accessToken())))
        .andExpect(status().isOk());
    mockMvc.perform(get("/api/users").header(HttpHeaders.AUTHORIZATION, bearer(first.accessToken())))
        .andExpect(status().isUnauthorized());
  }


  @Test
  @DisplayName("위조된 엑세스 토큰으로는 인증되지 않는다")
  void forgedAccessToken_returns401() throws Exception {
    signUp("forged", "forged@email.com");
    Tokens tokens = login("forged");

    // 서명 부분만 바꾼 토큰
    String forged = tokens.accessToken().substring(0, tokens.accessToken().lastIndexOf('.')) + ".x";

    mockMvc.perform(get("/api/users").header(HttpHeaders.AUTHORIZATION, bearer(forged)))
        .andExpect(status().isUnauthorized());
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
