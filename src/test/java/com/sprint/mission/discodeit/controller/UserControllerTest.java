package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.user.UserDuplicateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

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
  @MockitoBean
  private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  @DisplayName("사용자를 생성할 수 있다")
  void createUser_success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    UserCreateRequest request = new UserCreateRequest(
        "testUser",
        "test@test.com",
        "password"
    );

    UserResponse response = new UserResponse(
        userId,
        "testUser",
        "test@test.com",
        null,
        true
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
        "fake-image-content".getBytes()
    );

    given(userService.create(any(UserCreateRequest.class), any()))
        .willReturn(response);

    // when & then
    mockMvc.perform(multipart("/api/users")
            .file(requestPart)
            .file(profile)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("testUser"))
        .andExpect(jsonPath("$.email").value("test@test.com"))
        .andExpect(jsonPath("$.online").value(true));

    verify(userService).create(any(UserCreateRequest.class), any(MultipartFile.class));
  }

  @Test
  @DisplayName("중복된 사용자 생성 요청이면 실패한다")
  void createUser_fail_duplicateUser() throws Exception {
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

    given(userService.create(any(UserCreateRequest.class), any()))
        .willThrow(UserDuplicateException.withUsername("testUser"));

    // when & then
    mockMvc.perform(multipart("/api/users")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("사용자 정보를 수정할 수 있다")
  void updateUser_success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    UserUpdateRequest request = new UserUpdateRequest(
        "newUser",
        "new@test.com",
        "newPassword"
    );

    UserResponse response = new UserResponse(
        userId,
        "newUser",
        "new@test.com",
        null,
        true
    );

    MockMultipartFile requestPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    given(userService.update(eq(userId), any(UserUpdateRequest.class), any()))
        .willReturn(response);

    // when & then
    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(mockRequest -> {
              mockRequest.setMethod("PATCH");
              return mockRequest;
            }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("newUser"))
        .andExpect(jsonPath("$.email").value("new@test.com"))
        .andExpect(jsonPath("$.online").value(true));

    verify(userService).update(eq(userId), any(UserUpdateRequest.class), any());
  }

  @Test
  @DisplayName("존재하지 않는 사용자 수정 요청이면 실패한다")
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

    given(userService.update(eq(userId), any(UserUpdateRequest.class), any()))
        .willThrow(new UserNotFoundException(userId));

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
  @DisplayName("사용자를 삭제할 수 있다")
  void deleteUser_success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    doNothing()
        .when(userService)
        .delete(userId);

    // when & then
    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());

    verify(userService).delete(userId);
  }

  @Test
  @DisplayName("존재하지 않는 사용자 삭제 요청이면 실패한다")
  void deleteUser_fail_userNotFound() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    doThrow(new UserNotFoundException(userId))
        .when(userService)
        .delete(userId);

    // when & then
    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("전체 사용자를 조회할 수 있다")
  void findAll_success() throws Exception {
    // given
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();

    List<UserResponse> responses = List.of(
        new UserResponse(
            userId1,
            "user1",
            "user1@test.com",
            null,
            true
        ),
        new UserResponse(
            userId2,
            "user2",
            "user2@test.com",
            null,
            false
        )
    );

    given(userService.findAll())
        .willReturn(responses);

    // when & then
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(userId1.toString()))
        .andExpect(jsonPath("$[0].username").value("user1"))
        .andExpect(jsonPath("$[0].email").value("user1@test.com"))
        .andExpect(jsonPath("$[0].online").value(true))
        .andExpect(jsonPath("$[1].id").value(userId2.toString()))
        .andExpect(jsonPath("$[1].username").value("user2"))
        .andExpect(jsonPath("$[1].email").value("user2@test.com"))
        .andExpect(jsonPath("$[1].online").value(false));
  }

  @Test
  @DisplayName("사용자가 없으면 빈 목록을 반환한다")
  void findAll_empty() throws Exception {
    // given
    given(userService.findAll())
        .willReturn(List.of());

    // when & then
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
  }

}