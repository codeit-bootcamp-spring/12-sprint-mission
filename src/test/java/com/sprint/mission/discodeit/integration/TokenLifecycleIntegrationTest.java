package com.sprint.mission.discodeit.integration;

import static com.sprint.mission.discodeit.support.SecurityTestSupport.asUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

/**
 * 트랜잭션 커밋 이후에 일어나는 토큰 동작을 검증한다.
 *
 * <p>클래스 레벨 {@code @Transactional}을 쓰지 않는다. 권한 변경에 따른 토큰 무효화는
 * {@code @TransactionalEventListener}가 커밋 이후에 수행하므로, 롤백되는 테스트에서는
 * 리스너가 아예 실행되지 않아 검증할 수 없다.
 *
 * <p>유예 창은 기본값을 그대로 쓴다. 유예 창을 끈 상태의 재사용 감지는 AuthIntegrationTest에서 본다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TokenLifecycleIntegrationTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  private record Tokens(String accessToken, String refreshToken) {

  }

  private String signUp(String username) throws Exception {
    MockMultipartFile part = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new UserCreateRequest(username, username + "@email.com", "password123!")));
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
    return new Tokens(body.get("accessToken").asText(),
        cookie == null ? null : cookie.getValue());
  }

  @Test
  @DisplayName("권한이 변경되면 커밋 이후에 로그인 중이던 토큰이 무효화된다")
  void updateRole_forcesLogoutAfterCommit() throws Exception {
    String targetId = signUp("rolecommit");
    Tokens tokens = login("rolecommit");

    mockMvc.perform(put("/api/auth/role")
            .with(asUser(UUID.randomUUID(), "admin", Role.ADMIN))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new UserRoleUpdateRequest(UUID.fromString(targetId), Role.CHANNEL_MANAGER))))
        .andExpect(status().isOk());

    // 토큰에는 발급 시점의 권한이 박혀 있으므로, 재로그인을 강제해야 변경된 권한이 반영된다
    mockMvc.perform(get("/api/users")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.accessToken()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("탭 두 개가 같은 리프레시 토큰으로 재발급해도 둘 다 성공하고 같은 토큰을 받는다")
  void refresh_duplicateWithinGraceWindow_doesNotForceLogout() throws Exception {
    signUp("gracetab");
    Tokens tokens = login("gracetab");

    MvcResult first = mockMvc.perform(post("/api/auth/refresh")
            .cookie(new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, tokens.refreshToken()))
            .with(csrf()))
        .andExpect(status().isOk())
        .andReturn();

    // 두 번째 탭이 아직 로테이션 사실을 모른 채 같은 쿠키로 뒤늦게 도착한다.
    // 유예 창이 없으면 여기서 재사용으로 판정되어 정상 사용자가 강제 로그아웃된다.
    MvcResult second = mockMvc.perform(post("/api/auth/refresh")
            .cookie(new Cookie(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, tokens.refreshToken()))
            .with(csrf()))
        .andExpect(status().isOk())
        .andReturn();

    String firstAccessToken =
        objectMapper.readTree(first.getResponse().getContentAsString()).get("accessToken").asText();
    String secondAccessToken =
        objectMapper.readTree(second.getResponse().getContentAsString()).get("accessToken")
            .asText();

    // 두 탭이 서로 다른 토큰을 받으면 한쪽이 갱신할 때 다른 쪽이 죽는다
    assertThat(secondAccessToken).isEqualTo(firstAccessToken);

    mockMvc.perform(get("/api/users")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + firstAccessToken))
        .andExpect(status().isOk());
  }
}
