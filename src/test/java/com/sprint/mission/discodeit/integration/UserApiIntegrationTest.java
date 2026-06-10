package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import java.nio.charset.StandardCharsets;
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
class UserApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("사용자를 생성할 수 있다")
  void createUser_success() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "testUser",
        "test@test.com",
        "password"
    );

    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    MockMultipartFile profile = new MockMultipartFile(
        "profile",
        "profile.png",
        MediaType.IMAGE_PNG_VALUE,
        "fake-image-content".getBytes(StandardCharsets.UTF_8)
    );

    // when & then
    mockMvc.perform(multipart("/api/users")
            .file(requestPart)
            .file(profile)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.username").value("testUser"))
        .andExpect(jsonPath("$.email").value("test@test.com"))
        .andExpect(jsonPath("$.online").value(true));
  }

  @Test
  @DisplayName("전체 사용자를 조회할 수 있다")
  void findAllUsers_success() throws Exception {
    // given
    createUser("user1", "user1@test.com");
    createUser("user2", "user2@test.com");

    // when & then
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").exists())
        .andExpect(jsonPath("$[0].username").exists())
        .andExpect(jsonPath("$[0].email").exists());
  }

  @Test
  @DisplayName("사용자 정보를 수정할 수 있다")
  void updateUser_success() throws Exception {
    // given
    UUID userId = createUser("testUser", "test@test.com");

    UserUpdateRequest request = new UserUpdateRequest(
        "newUser",
        "",
        "newPassword"
    );

    MockMultipartFile requestPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    MockMultipartFile profile = new MockMultipartFile(
        "profile",
        "new-profile.png",
        MediaType.IMAGE_PNG_VALUE,
        "new-fake-image-content".getBytes(StandardCharsets.UTF_8)
    );

    // when & then
    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(requestPart)
            .file(profile)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(mockRequest -> {
              mockRequest.setMethod("PATCH");
              return mockRequest;
            }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("newUser"))
        .andExpect(jsonPath("$.email").value("test@test.com"))
        .andExpect(jsonPath("$.online").value(true));
  }

  @Test
  @DisplayName("사용자를 삭제할 수 있다")
  void deleteUser_success() throws Exception {
    // given
    UUID userId = createUser("testUser", "test@test.com");

    // when & then
    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("존재하지 않는 사용자 수정 요청이면 404를 반환한다")
  void updateUser_fail_userNotFound() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    UserUpdateRequest request = new UserUpdateRequest(
        "newUser",
        "new@test.com",
        "newPassword"
    );

    MockMultipartFile requestPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    // when & then
    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(mockRequest -> {
              mockRequest.setMethod("PATCH");
              return mockRequest;
            }))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("존재하지 않는 사용자 삭제 요청이면 404를 반환한다")
  void deleteUser_fail_userNotFound() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    // when & then
    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNotFound());
  }

  private UUID createUser(String username, String email) throws Exception {
    UserCreateRequest request = new UserCreateRequest(
        username,
        email,
        "password"
    );

    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    MvcResult result = mockMvc.perform(multipart("/api/users")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    JsonNode root = objectMapper.readTree(responseBody);

    return UUID.fromString(root.get("id").asText());
  }
}