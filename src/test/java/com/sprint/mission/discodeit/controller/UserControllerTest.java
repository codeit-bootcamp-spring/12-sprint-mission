package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private UserStatusService userStatusService;

  @Test
  @DisplayName("사용자 생성 성공")
  void create_success() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "minji",
        "minji@test.com",
        "1234"
    );

    UserDto response = new UserDto(
        UUID.randomUUID(),
        "minji",
        "minji@test.com",
        null,
        false
    );

    given(userService.create(any(UserCreateRequest.class), eq(Optional.empty())))
        .willReturn(response);

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
        .andExpect(jsonPath("$.email").value("minji@test.com"))
        .andExpect(jsonPath("$.online").value(false));

    then(userService).should()
        .create(any(UserCreateRequest.class), eq(Optional.empty()));
  }

  @Test
  @DisplayName("사용자 생성 실패 - 요청값 검증 실패")
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
    UUID userId = UUID.randomUUID();

    UserUpdateRequest request = new UserUpdateRequest(
        "newName",
        "new@test.com",
        "newPassword"
    );

    UserDto response = new UserDto(
        userId,
        "newName",
        "new@test.com",
        null,
        true
    );

    given(userService.update(eq(userId), any(UserUpdateRequest.class), eq(Optional.empty())))
        .willReturn(response);

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
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("newName"))
        .andExpect(jsonPath("$.email").value("new@test.com"))
        .andExpect(jsonPath("$.online").value(true));

    then(userService).should()
        .update(eq(userId), any(UserUpdateRequest.class), eq(Optional.empty()));
  }

  @Test
  @DisplayName("사용자 수정 실패 - 사용자 없음")
  void update_fail_userNotFound() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    UserUpdateRequest request = new UserUpdateRequest(
        "newName",
        "new@test.com",
        "newPassword"
    );

    given(userService.update(eq(userId), any(UserUpdateRequest.class), eq(Optional.empty())))
        .willThrow(new UserNotFoundException(userId));

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
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("사용자 삭제 성공")
  void delete_success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    // when & then
    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());

    then(userService).should().delete(userId);
  }

  @Test
  @DisplayName("사용자 삭제 실패 - 사용자 없음")
  void delete_fail_userNotFound() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    org.mockito.BDDMockito.willThrow(new UserNotFoundException(userId))
        .given(userService)
        .delete(userId);

    // when & then
    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("사용자 목록 조회 성공")
  void findAll_success() throws Exception {
    // given
    UserDto user = new UserDto(
        UUID.randomUUID(),
        "minji",
        "minji@test.com",
        null,
        true
    );

    given(userService.findAll())
        .willReturn(List.of(user));

    // when & then
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value("minji"))
        .andExpect(jsonPath("$[0].email").value("minji@test.com"))
        .andExpect(jsonPath("$[0].online").value(true));
  }
}