package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.Optional;
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
class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserService userService;

  @Autowired
  private UserStatusService userStatusService;

  // ── create ──────────────────────────────────────────────

  @Test
  @DisplayName("사용자 생성 성공")
  void createUser_Success() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest("testUser", "test@example.com",
        "password123");
    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request));
    MockMultipartFile profilePart = new MockMultipartFile(
        "profile", "profile.jpg", MediaType.IMAGE_JPEG_VALUE, "image-data".getBytes());

    // when & then
    mockMvc.perform(multipart("/api/user")
            .file(userPart)
            .file(profilePart))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.username", is("testUser")))
        .andExpect(jsonPath("$.email", is("test@example.com")));
  }

  @Test
  @DisplayName("유효하지 않은 요청으로 사용자 생성 시 400 반환")
  void createUser_InvalidRequest_Returns400() throws Exception {
    // given — username 최소 길이(2) 위반, 이메일 형식 위반, password 최소 길이(8) 위반
    UserCreateRequest request = new UserCreateRequest("t", "invalid-email", "short");
    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request));

    // when & then
    mockMvc.perform(multipart("/api/user").file(userPart))
        .andExpect(status().isBadRequest());
  }

  // ── findAll ──────────────────────────────────────────────

  @Test
  @DisplayName("사용자 목록 조회 성공")
  void findAllUsers_Success() throws Exception {
    // given
    userService.create(new UserCreateRequest("user1", "user1@example.com", "password123"),
        Optional.empty());
    userService.create(new UserCreateRequest("user2", "user2@example.com", "password123"),
        Optional.empty());

    // when & then
    mockMvc.perform(get("/api/user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].username", is("user1")))
        .andExpect(jsonPath("$[1].username", is("user2")));
  }

  // ── update ──────────────────────────────────────────────

  @Test
  @DisplayName("사용자 수정 성공")
  void updateUser_Success() throws Exception {
    // given
    UserDto created = userService.create(
        new UserCreateRequest("originalUser", "original@example.com", "password123"),
        Optional.empty());
    UUID userId = created.id();

    UserUpdateRequest updateRequest = new UserUpdateRequest("updatedUser", "updated@example.com",
        "newpassword123");
    MockMultipartFile updatePart = new MockMultipartFile(
        "userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(updateRequest));

    // when & then
    mockMvc.perform(multipart("/api/user/{userId}", userId)
            .file(updatePart)
            .with(req -> {
              req.setMethod("PATCH");
              return req;
            }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(userId.toString())))
        .andExpect(jsonPath("$.username", is("updatedUser")))
        .andExpect(jsonPath("$.email", is("updated@example.com")));
  }

  @Test
  @DisplayName("존재하지 않는 사용자 수정 시 404 반환")
  void updateUser_NotFound_Returns404() throws Exception {
    // given
    UserUpdateRequest updateRequest = new UserUpdateRequest("nobody", "nobody@example.com",
        "password123");
    MockMultipartFile updatePart = new MockMultipartFile(
        "userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(updateRequest));

    // when & then
    mockMvc.perform(multipart("/api/user/{userId}", UUID.randomUUID())
            .file(updatePart)
            .with(req -> {
              req.setMethod("PATCH");
              return req;
            }))
        .andExpect(status().isNotFound());
  }

  // ── delete ──────────────────────────────────────────────

  @Test
  @DisplayName("사용자 삭제 성공")
  void deleteUser_Success() throws Exception {
    // given
    UserDto created = userService.create(
        new UserCreateRequest("deleteUser", "delete@example.com", "password123"),
        Optional.empty());
    UUID userId = created.id();

    // when & then
    mockMvc.perform(delete("/api/user/{userId}", userId))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/user"))
        .andExpect(jsonPath("$[?(@.id == '" + userId + "')]").doesNotExist());
  }

  @Test
  @DisplayName("존재하지 않는 사용자 삭제 시 404 반환")
  void deleteUser_NotFound_Returns404() throws Exception {
    // when & then
    mockMvc.perform(delete("/api/user/{userId}", UUID.randomUUID()))
        .andExpect(status().isNotFound());
  }

  // ── updateUserStatus ─────────────────────────────────────

  @Test
  @DisplayName("사용자 상태 수정 성공")
  void updateUserStatus_Success() throws Exception {
    // given
    UserDto created = userService.create(
        new UserCreateRequest("statusUser", "status@example.com", "password123"),
        Optional.empty());
    UUID userId = created.id();

    Instant newLastActiveAt = Instant.now();
    UserStatusUpdateRequest statusRequest = new UserStatusUpdateRequest(newLastActiveAt);

    // when & then
    mockMvc.perform(patch("/api/user/{userId}/userStatus", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(statusRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lastActiveAt", is(newLastActiveAt.toString())));
  }

  @Test
  @DisplayName("존재하지 않는 사용자 상태 수정 시 404 반환")
  void updateUserStatus_NotFound_Returns404() throws Exception {
    // given
    UserStatusUpdateRequest statusRequest = new UserStatusUpdateRequest(Instant.now());

    // when & then
    mockMvc.perform(patch("/api/user/{userId}/userStatus", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(statusRequest)))
        .andExpect(status().isNotFound());
  }
}