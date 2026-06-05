package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
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

  // ── create ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("사용자 생성 성공 - 201")
  void create_success() throws Exception {
    UUID userId = UuidCreator.getTimeOrderedEpoch();
    BinaryContentDto avatarDto = new BinaryContentDto(UuidCreator.getTimeOrderedEpoch(),
        "avatar.png", 1024L, "image/png");
    UserDto userDto = new UserDto(userId, "testUser", "test@test.com", avatarDto, false);
    given(userService.create(any(UserCreateRequest.class), any())).willReturn(userDto);

    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new UserCreateRequest("testUser", "test@test.com", "password1234!")));
    MockMultipartFile profile = new MockMultipartFile(
        "profile", "avatar.png", MediaType.IMAGE_PNG_VALUE, "img".getBytes());

    mockMvc.perform(multipart("/api/users").file(requestPart).file(profile)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("testUser"))
        .andExpect(jsonPath("$.email").value("test@test.com"));
  }

  @Test
  @DisplayName("사용자 생성 실패 - 400 (유효성 검사 실패)")
  void create_fail_validationError() throws Exception {
    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new UserCreateRequest("t", "not-email", "short")));

    mockMvc.perform(multipart("/api/users").file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  // ── update ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("사용자 수정 성공 - 200")
  void update_success() throws Exception {
    UUID userId = UuidCreator.getTimeOrderedEpoch();
    UserDto updatedUser = new UserDto(userId, "updatedUser", "updated@test.com", null, false);
    given(userService.update(eq(userId), any(UserUpdateRequest.class), any())).willReturn(
        updatedUser);

    MockMultipartFile requestPart = new MockMultipartFile(
        "userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new UserUpdateRequest("updatedUser", "updated@test.com", null)));

    mockMvc.perform(multipart("/api/users/{userId}", userId).file(requestPart)
            .with(req -> {
              req.setMethod("PATCH");
              return req;
            })
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("updatedUser"))
        .andExpect(jsonPath("$.email").value("updated@test.com"));
  }

  @Test
  @DisplayName("사용자 수정 실패 - 404 (존재하지 않는 사용자)")
  void update_fail_userNotFound() throws Exception {
    UUID userId = UuidCreator.getTimeOrderedEpoch();
    given(userService.update(eq(userId), any(UserUpdateRequest.class), any()))
        .willThrow(UserNotFoundException.withId(userId));

    MockMultipartFile requestPart = new MockMultipartFile(
        "userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new UserUpdateRequest("updatedUser", null, null)));

    mockMvc.perform(multipart("/api/users/{userId}", userId).file(requestPart)
            .with(req -> {
              req.setMethod("PATCH");
              return req;
            })
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  // ── delete ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("사용자 삭제 성공 - 204")
  void delete_success() throws Exception {
    UUID userId = UuidCreator.getTimeOrderedEpoch();
    willDoNothing().given(userService).delete(userId);

    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("사용자 삭제 실패 - 404 (존재하지 않는 사용자)")
  void delete_fail_userNotFound() throws Exception {
    UUID userId = UuidCreator.getTimeOrderedEpoch();
    willThrow(UserNotFoundException.withId(userId)).given(userService).delete(userId);

    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  // ── findAll ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("사용자 목록 조회 성공 - 200")
  void findAll_success() throws Exception {
    UUID userId = UuidCreator.getTimeOrderedEpoch();
    UserDto userDto = new UserDto(userId, "testUser", "test@test.com", null, true);
    given(userService.findAll()).willReturn(List.of(userDto));

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(userId.toString()))
        .andExpect(jsonPath("$[0].username").value("testUser"));
  }

  @Test
  @DisplayName("사용자 목록 조회 성공 - 빈 목록 반환")
  void findAll_emptyList() throws Exception {
    given(userService.findAll()).willReturn(List.of());

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }

  // ── updateStatus ─────────────────────────────────────────────────────────

  @Test
  @DisplayName("사용자 상태 업데이트 성공 - 200")
  void updateStatus_success() throws Exception {
    UUID userId = UuidCreator.getTimeOrderedEpoch();
    Instant now = Instant.now();
    UserStatusDto statusDto = new UserStatusDto(UuidCreator.getTimeOrderedEpoch(), userId, now);
    given(userStatusService.updateByUserId(eq(userId),
        any(UserStatusUpdateRequest.class))).willReturn(statusDto);

    mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new UserStatusUpdateRequest(now))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(userId.toString()));
  }

  @Test
  @DisplayName("사용자 상태 업데이트 실패 - 404 (존재하지 않는 사용자)")
  void updateStatus_fail_userNotFound() throws Exception {
    UUID userId = UuidCreator.getTimeOrderedEpoch();
    given(userStatusService.updateByUserId(eq(userId), any(UserStatusUpdateRequest.class)))
        .willThrow(UserNotFoundException.withId(userId));

    mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new UserStatusUpdateRequest(Instant.now()))))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }
}
