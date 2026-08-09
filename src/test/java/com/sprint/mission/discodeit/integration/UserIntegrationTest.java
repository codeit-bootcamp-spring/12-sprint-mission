package com.sprint.mission.discodeit.integration;

import static com.sprint.mission.discodeit.support.SecurityTestSupport.asUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  private MockMultipartFile userPart(UserCreateRequest req) throws Exception {
    return new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(req));
  }

  private String createUserAndGetId(String username, String email) throws Exception {
    MockMultipartFile part = userPart(new UserCreateRequest(username, email, "password123!"));
    MvcResult result = mockMvc.perform(multipart("/api/users").file(part).with(csrf()))
        .andExpect(status().isCreated())
        .andReturn();
    JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
    return json.get("id").asText();
  }

  @Test
  @DisplayName("POST /api/users - 사용자 생성 성공 후 GET /api/users 목록 조회")
  void createAndFindAll() throws Exception {
    // 회원가입은 인증 없이 가능
    mockMvc.perform(multipart("/api/users")
            .file(userPart(new UserCreateRequest("integuser", "integ@email.com", "password123!")))
            .with(csrf()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("integuser"))
        .andExpect(jsonPath("$.email").value("integ@email.com"))
        .andExpect(jsonPath("$.role").value("USER"));

    // 목록 조회는 인증이 필요
    mockMvc.perform(get("/api/users").with(asUser(UUID.randomUUID(), "viewer", Role.USER)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.username == 'integuser')]").exists());
  }

  @Test
  @DisplayName("POST /api/users - 이메일 중복 시 400 반환")
  void createDuplicateEmail_returns400() throws Exception {
    mockMvc.perform(multipart("/api/users")
            .file(userPart(new UserCreateRequest("user1", "dup@email.com", "password123!")))
            .with(csrf()))
        .andExpect(status().isCreated());

    mockMvc.perform(multipart("/api/users")
            .file(userPart(new UserCreateRequest("user2", "dup@email.com", "password123!")))
            .with(csrf()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("DUPLICATE_USER"));
  }

  @Test
  @DisplayName("PATCH /api/users/{userId} - 본인 정보 수정 성공")
  void updateUser_success() throws Exception {
    String userId = createUserAndGetId("before", "before@email.com");

    MockMultipartFile updatePart = new MockMultipartFile(
        "userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new UserUpdateRequest("after", "after@email.com", "newpassword1!")));

    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(updatePart)
            .with(req -> {
              req.setMethod("PATCH");
              return req;
            })
            .with(asUser(userId, "before", Role.USER))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("after"));
  }

  @Test
  @DisplayName("PATCH /api/users/{userId} - 다른 사용자의 정보를 수정하면 403 반환")
  void updateOtherUser_returns403() throws Exception {
    String userId = createUserAndGetId("owner", "owner@email.com");

    MockMultipartFile updatePart = new MockMultipartFile(
        "userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new UserUpdateRequest("hacked", "hacked@email.com", "password123!")));

    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(updatePart)
            .with(req -> {
              req.setMethod("PATCH");
              return req;
            })
            .with(asUser(UUID.randomUUID(), "other", Role.USER))
            .with(csrf()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
  }

  @Test
  @DisplayName("DELETE /api/users/{userId} - 삭제 성공 후 재삭제 시 404")
  void deleteUser_thenRemovedFromList() throws Exception {
    String userId = createUserAndGetId("todelete", "todelete@email.com");

    mockMvc.perform(delete("/api/users/{userId}", userId)
            .with(asUser(userId, "todelete", Role.USER))
            .with(csrf()))
        .andExpect(status().isNoContent());

    // 두 번 삭제하면 404
    mockMvc.perform(delete("/api/users/{userId}", userId)
            .with(asUser(userId, "todelete", Role.USER))
            .with(csrf()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }
}
