package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("사용자 생성 성공")
  void create_success() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "minji",
        "minji@test.com",
        "1234"
    );

    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    // when & then
    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequest))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("minji"))
        .andExpect(jsonPath("$.email").value("minji@test.com"));
  }

  @Test
  @DisplayName("사용자 생성 실패 - 검증 실패")
  void create_fail_validation() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "",
        "wrong-email",
        "1"
    );

    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    // when & then
    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequest))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 수정 성공")
  void update_success() throws Exception {
    // given
    UUID userId = createUser("oldName", "old@test.com");

    UserUpdateRequest request = new UserUpdateRequest(
        "newName",
        "new@test.com",
        "newPassword"
    );

    MockMultipartFile userUpdateRequest = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    // when & then
    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(userUpdateRequest)
            .with(servletRequest -> {
              servletRequest.setMethod("PATCH");
              return servletRequest;
            }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("newName"))
        .andExpect(jsonPath("$.email").value("new@test.com"));
  }

  @Test
  @DisplayName("사용자 삭제 성공")
  void delete_success() throws Exception {
    // given
    UUID userId = createUser("deleteUser", "delete@test.com");

    // when & then
    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("사용자 목록 조회 성공")
  void findAll_success() throws Exception {
    // given
    createUser("listUser", "list@test.com");

    // when & then
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").exists())
        .andExpect(jsonPath("$[0].email").exists());
  }

  private UUID createUser(String username, String email) throws Exception {
    UserCreateRequest request = new UserCreateRequest(
        username,
        email,
        "1234"
    );

    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    String responseBody = mockMvc.perform(multipart("/api/users")
            .file(userCreateRequest))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode jsonNode = objectMapper.readTree(responseBody);
    return UUID.fromString(jsonNode.get("id").asText());
  }
}