package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.MDCLoggingInterceptor;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
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
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import({GlobalExceptionHandler.class, MDCLoggingInterceptor.class})
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
    @DisplayName("사용자 생성 성공")
    void create_success() throws Exception {
        UUID userId = UUID.randomUUID();

        UserCreateRequest request = new UserCreateRequest(
                "user1",
                "user1@test.com",
                "password"
        );

        UserResponse response = new UserResponse(
                userId,
                "user1",
                "user1@test.com",
                null,
                false
        );

        MockMultipartFile userCreateRequestPart = new MockMultipartFile(
                "userCreateRequest",
                "userCreateRequest.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        when(userService.create(eq(request), eq(Optional.empty())))
                .thenReturn(response);

        mockMvc.perform(multipart("/api/users")
                        .file(userCreateRequestPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.email").value("user1@test.com"))
                .andExpect(jsonPath("$.online").value(false));

        verify(userService).create(eq(request), eq(Optional.empty()));
    }

    @Test
    @DisplayName("사용자 생성 실패 - 요청 값 검증 실패")
    void create_fail_invalidRequest() throws Exception {
        UserCreateRequest request = new UserCreateRequest(
                "u",
                "invalid-email",
                "123"
        );

        MockMultipartFile userCreateRequestPart = new MockMultipartFile(
                "userCreateRequest",
                "userCreateRequest.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/users")
                        .file(userCreateRequestPart))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("사용자 단건 조회 성공")
    void findById_success() throws Exception {
        UUID userId = UUID.randomUUID();

        UserResponse response = new UserResponse(
                userId,
                "user1",
                "user1@test.com",
                null,
                true
        );

        when(userService.findById(userId)).thenReturn(response);

        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.email").value("user1@test.com"))
                .andExpect(jsonPath("$.online").value(true));

        verify(userService).findById(userId);
    }

    @Test
    @DisplayName("사용자 단건 조회 실패 - 사용자 없음")
    void findById_fail_userNotFound() throws Exception {
        UUID userId = UUID.randomUUID();

        when(userService.findById(userId)).thenThrow(new UserNotFoundException(userId));

        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.exceptionType").value("UserNotFoundException"));

        verify(userService).findById(userId);
    }

    @Test
    @DisplayName("사용자 목록 조회 성공")
    void findAll_success() throws Exception {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        UserResponse response1 = new UserResponse(
                userId1,
                "user1",
                "user1@test.com",
                null,
                true
        );

        UserResponse response2 = new UserResponse(
                userId2,
                "user2",
                "user2@test.com",
                null,
                false
        );

        when(userService.findAll()).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userId1.toString()))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].id").value(userId2.toString()))
                .andExpect(jsonPath("$[1].username").value("user2"));

        verify(userService).findAll();
    }

    @Test
    @DisplayName("사용자 수정 성공")
    void update_success() throws Exception {
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

        MockMultipartFile userUpdateRequestPart = new MockMultipartFile(
                "userUpdateRequest",
                "userUpdateRequest.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        when(userService.update(eq(userId), eq(request), eq(Optional.empty())))
                .thenReturn(response);

        mockMvc.perform(multipart("/api/users/{userId}", userId)
                        .file(userUpdateRequestPart)
                        .with(requestBuilder -> {
                            requestBuilder.setMethod("PATCH");
                            return requestBuilder;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("newUser"))
                .andExpect(jsonPath("$.email").value("new@test.com"));

        verify(userService).update(eq(userId), eq(request), eq(Optional.empty()));
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void delete_success() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userService).delete(userId);
    }
}