package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.rest.UserController;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  @DisplayName("user_create_success")
  void user_create_success() throws Exception {
    UserCreateRequest userCreateRequest = new UserCreateRequest(
        "username",
        "email@email.com",
        "password123!"
    );

    MockMultipartFile userCreateRequestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(userCreateRequest)
    );

    MockMultipartFile profilePart = new MockMultipartFile(
        "profile",
        "profile.png",
        "image/png",
        "profile".getBytes()
    );

    BinaryContent profile = BinaryContent.builder()
        .id(UUID.randomUUID())
        .fileName(profilePart.getOriginalFilename())
        .contentType(profilePart.getContentType())
        .size((long) profilePart.getBytes().length)
        .build();

    BinaryContentDto profileDto = new BinaryContentDto(
        profile.getId(),
        profile.getFileName(),
        profile.getSize(),
        profile.getContentType()
    );

    UserDto userDto = new UserDto(
        UUID.randomUUID(),
        userCreateRequest.username(),
        userCreateRequest.email(),
        profileDto,
        true
    );

    given(userService.create(eq(userCreateRequest), any())).willReturn(userDto);

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequestPart)
            .file(profilePart))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(userDto.id().toString()))
        .andExpect(jsonPath("$.username").value(userDto.username()))
        .andExpect(jsonPath("$.email").value(userDto.email()))
        .andExpect(jsonPath("$.profile.id").value(profileDto.id().toString()))
        .andExpect(jsonPath("$.profile.fileName").value(profileDto.fileName()))
        .andExpect(jsonPath("$.profile.size").value(profileDto.size()))
        .andExpect(jsonPath("$.profile.contentType").value(profileDto.contentType()))
        .andExpect(jsonPath("$.online").value(userDto.online()));
  }

  @Test
  @DisplayName("user_create_fail")
  void user_create_fail() throws Exception {
    UserCreateRequest userCreateRequest = new UserCreateRequest(
        "username",
        "email@email.com",
        "error"
    );

    MockMultipartFile userCreateRequestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(userCreateRequest)
    );

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequestPart))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  @DisplayName("find_all")
  void find_all() throws Exception {
    UserDto userDto1 = new UserDto(
        UUID.randomUUID(),
        "username1",
        "email1@email.com",
        null,
        false
    );

    UserDto userDto2 = new UserDto(
        UUID.randomUUID(),
        "username2",
        "email2@email.com",
        null,
        true
    );

    given(userService.findAllWithFetch())
        .willReturn(List.of(userDto1, userDto2));

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("$[0].id").value(userDto1.id().toString()))
        .andExpect(jsonPath("$[0].username").value(userDto1.username()))
        .andExpect(jsonPath("$[0].email").value(userDto1.email()))
        .andExpect(jsonPath("$[0].profile").doesNotExist())
        .andExpect(jsonPath("$[0].online").value(userDto1.online()))
        .andExpect(jsonPath("$[1].id").value(userDto2.id().toString()))
        .andExpect(jsonPath("$[1].username").value(userDto2.username()))
        .andExpect(jsonPath("$[1].email").value(userDto2.email()))
        .andExpect(jsonPath("$[1].profile").doesNotExist())
        .andExpect(jsonPath("$[1].online").value(userDto2.online()));
  }

  @Test
  @DisplayName("update_user_success")
  void update_user_success() throws Exception {
    UUID userId = UUID.randomUUID();

    UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
        "username",
        "email@email.com",
        "password123!"
    );

    MockMultipartFile userUpdateRequestPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(userUpdateRequest)
    );

    UserDto userDto = new UserDto(
        userId,
        userUpdateRequest.newUsername(),
        userUpdateRequest.newEmail(),
        null,
        true
    );

    given(userService.update(eq(userId), eq(userUpdateRequest), any()))
        .willReturn(userDto);

    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(userUpdateRequestPart)
            .with(request -> {
              request.setMethod("PATCH");
              return request;
            }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userDto.id().toString()))
        .andExpect(jsonPath("$.username").value(userDto.username()))
        .andExpect(jsonPath("$.email").value(userDto.email()))
        .andExpect(jsonPath("$.profile").doesNotExist())
        .andExpect(jsonPath("$.online").value(userDto.online()));
  }

  @Test
  @DisplayName("update_user_fail")
  void update_user_fail() throws Exception {
    UUID userId = UUID.randomUUID();

    UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
        "username",
        "email@email.com",
        "error"
    );

    MockMultipartFile userUpdateRequestPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(userUpdateRequest)
    );

    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(userUpdateRequestPart)
            .with(request -> {
              request.setMethod("PATCH");
              return request;
            }))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  @DisplayName("delete_user_success")
  void delete_user_success() throws Exception {
    UUID userId = UUID.randomUUID();

    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());

    verify(userService, times(1)).delete(eq(userId));
  }

  @Test
  @DisplayName("delete_user_fail")
  void delete_user_fail() throws Exception {
    UUID userId = UUID.randomUUID();

    willThrow(UserNotFoundException.withUserId(userId))
        .given(userService)
        .delete(userId);

    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }
}
