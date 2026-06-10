package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private UserStatusService userStatusService;

  private UUID userId;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    userDto = new UserDto(userId, "testUser", "test@example.com", null, false);
  }

  // ── create ──────────────────────────────────────────────

  @Test
  @DisplayName("사용자 생성 성공")
  void createUser_Success() throws Exception {
    // given
    given(userService.create(any(), any())).willReturn(userDto);

    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new com.sprint.mission.discodeit.dto.request.user.UserCreateRequest(
                "testUser", "test@example.com", "password123"))
    );

    // when & then
    mockMvc.perform(multipart("/api/user")
            .file(userCreateRequest))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("testUser"))
        .andExpect(jsonPath("$.email").value("test@example.com"));
  }

  @Test
  @DisplayName("사용자 생성 시 필수 필드 누락이면 400 반환")
  void createUser_InvalidRequest_Returns400() throws Exception {
    // given
    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new com.sprint.mission.discodeit.dto.request.user.UserCreateRequest(
                "", "", ""))
    );

    // when & then
    mockMvc.perform(multipart("/api/user")
            .file(userCreateRequest))
        .andExpect(status().isBadRequest());
  }

  // ── update ──────────────────────────────────────────────

  @Test
  @DisplayName("사용자 수정 성공")
  void updateUser_Success() throws Exception {
    // given
    given(userService.update(eq(userId), any(), any())).willReturn(userDto);

    MockMultipartFile userUpdateRequest = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest(
                "newUser", "new@example.com", "newpassword123"))
    );

    // when & then
    mockMvc.perform(multipart("/api/user/{userId}", userId)
            .file(userUpdateRequest)
            .with(request -> {
              request.setMethod("PATCH");
              return request;
            }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("testUser"));
  }

  @Test
  @DisplayName("존재하지 않는 사용자 수정 시 404 반환")
  void updateUser_NotFound_Returns404() throws Exception {
    // given
    given(userService.update(eq(userId), any(), any()))
        .willThrow(new UserNotFoundException());

    MockMultipartFile userUpdateRequest = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest(
                "newUser", "new@example.com", "newpassword123"))
    );

    // when & then
    mockMvc.perform(multipart("/api/user/{userId}", userId)
            .file(userUpdateRequest)
            .with(request -> {
              request.setMethod("PATCH");
              return request;
            }))
        .andExpect(status().isNotFound());
  }

  // ── delete ──────────────────────────────────────────────

  @Test
  @DisplayName("사용자 삭제 성공")
  void deleteUser_Success() throws Exception {
    // given
    doNothing().when(userService).delete(eq(userId));

    // when & then
    mockMvc.perform(delete("/api/user/{userId}", userId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("존재하지 않는 사용자 삭제 시 404 반환")
  void deleteUser_NotFound_Returns404() throws Exception {
    // given
    doThrow(new UserNotFoundException()).when(userService).delete(eq(userId));

    // when & then
    mockMvc.perform(delete("/api/user/{userId}", userId))
        .andExpect(status().isNotFound());
  }

  // ── findAll ──────────────────────────────────────────────

  @Test
  @DisplayName("전체 사용자 조회 성공")
  void findAll_Success() throws Exception {
    // given
    given(userService.findAll()).willReturn(List.of(userDto));

    // when & then
    mockMvc.perform(get("/api/user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].username").value("testUser"));
  }

  @Test
  @DisplayName("사용자 없으면 빈 목록 반환")
  void findAll_Empty_ReturnsEmptyList() throws Exception {
    // given
    given(userService.findAll()).willReturn(List.of());

    // when & then
    mockMvc.perform(get("/api/user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
  }
}