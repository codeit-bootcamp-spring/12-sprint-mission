package com.sprint.mission.discodeit.controller;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@Import(com.sprint.mission.discodeit.mapper.PageResponseMapper.class)
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private UserStatusService userStatusService;


	@Test
	@DisplayName("POST /api/users - 성공")
	void create_Success() throws Exception {
		UserCreateRequest request = new UserCreateRequest("testuser", "test@example.com", "password123");
		BinaryContentDto mockProfileDto = mock(BinaryContentDto.class);
		UserDto expectedResponse = new UserDto(UUID.randomUUID(), "testuser", "test@example.com", mockProfileDto, true);

		given(userService.create(any(UserCreateRequest.class), any())).willReturn(expectedResponse);

		MockMultipartFile userRequestPart = new MockMultipartFile(
			"userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
		);
		MockMultipartFile profilePart = new MockMultipartFile(
			"profile", "profile.png", MediaType.IMAGE_PNG_VALUE, new byte[]{1, 2, 3}
		);

		mockMvc.perform(multipart("/api/users")
				.file(userRequestPart)
				.file(profilePart))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.username").value("testuser"))
			.andExpect(jsonPath("$.email").value("test@example.com"));
	}

	@Test
	@DisplayName("POST /api/users - 실패")
	void create_Fail_AlreadyExists() throws Exception {
		UserCreateRequest request = new UserCreateRequest("duplicateUser", "exist@example.com", "password123");

		given(userService.create(any(UserCreateRequest.class), any()))
			.willThrow(UserAlreadyExistException.class);

		MockMultipartFile userRequestPart = new MockMultipartFile(
			"userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
		);

		mockMvc.perform(multipart("/api/users")
				.file(userRequestPart))
			.andExpect(status().isConflict());
	}

	@Test
	@DisplayName("PATCH /api/users/{userId} - 성공")
	void update_Success() throws Exception {
		UUID userId = UUID.randomUUID();
		UserUpdateRequest updateRequest = new UserUpdateRequest("newUsername", "new@example.com", "newpass123");
		BinaryContentDto mockProfileDto = mock(BinaryContentDto.class);
		UserDto expectedResponse = new UserDto(userId, "newUsername", "new@example.com", mockProfileDto, true);

		given(userService.update(eq(userId), any(UserUpdateRequest.class), any())).willReturn(expectedResponse);

		MockMultipartFile userUpdateRequestPart = new MockMultipartFile(
			"userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsString(updateRequest).getBytes(StandardCharsets.UTF_8)
		);

		mockMvc.perform(multipart("/api/users/{userId}", userId)
				.file(userUpdateRequestPart)
				.with(request -> { request.setMethod("PATCH"); return request; }))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.username").value("newUsername"))
			.andExpect(jsonPath("$.email").value("new@example.com"));
	}

	@Test
	@DisplayName("PATCH /api/users/{userId} - 실패")
	void update_Fail_UserNotFound() throws Exception {
		UUID invalidUserId = UUID.randomUUID();
		UserUpdateRequest updateRequest = new UserUpdateRequest("ghost", "ghost@example.com", "ghost123");

		given(userService.update(eq(invalidUserId), any(UserUpdateRequest.class), any()))
			.willThrow(UserNotFoundException.class);

		MockMultipartFile userUpdateRequestPart = new MockMultipartFile(
			"userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsString(updateRequest).getBytes(StandardCharsets.UTF_8)
		);

		mockMvc.perform(multipart("/api/users/{userId}", invalidUserId)
				.file(userUpdateRequestPart)
				.with(request -> { request.setMethod("PATCH"); return request; }))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("DELETE /api/users/{userId} - 성공")
	void delete_Success() throws Exception {
		UUID userId = UUID.randomUUID();
		willDoNothing().given(userService).delete(userId);

		mockMvc.perform(delete("/api/users/{userId}", userId))
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("DELETE /api/users/{userId} - 실패")
	void delete_Fail_UserNotFound() throws Exception {
		UUID invalidUserId = UUID.randomUUID();
		willThrow(UserNotFoundException.class).given(userService).delete(invalidUserId);

		mockMvc.perform(delete("/api/users/{userId}", invalidUserId))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("GET /api/users - 성공")
	void findAll_Success() throws Exception {
		BinaryContentDto mockProfileDto = mock(BinaryContentDto.class);
		UserDto user1 = new UserDto(UUID.randomUUID(), "user1", "user1@example.com", mockProfileDto, true);
		UserDto user2 = new UserDto(UUID.randomUUID(), "user2", "user2@example.com", mockProfileDto, true);

		given(userService.findAll()).willReturn(List.of(user1, user2));

		mockMvc.perform(get("/api/users"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(2))
			.andExpect(jsonPath("$[0].username").value("user1"))
			.andExpect(jsonPath("$[1].username").value("user2"));
	}

	@Test
	@DisplayName("GET /api/users - 실패")
	void findAll_Success_EmptyList() throws Exception {
		given(userService.findAll()).willReturn(List.of());

		mockMvc.perform(get("/api/users"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(0));
	}

	@Test
	@DisplayName("PATCH /api/users/{userId}/userStatus - 성공")
	void updateUserStatusByUserId_Success() throws Exception {
		UUID userId = UUID.randomUUID();
		UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());
		UserStatusDto expectedResponse = new UserStatusDto(UUID.randomUUID(), userId, Instant.now());

		given(userStatusService.updateByUserId(eq(userId), any(UserStatusUpdateRequest.class))).willReturn(expectedResponse);

		mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("ONLINE"));
	}

	@Test
	@DisplayName("PATCH /api/users/{userId}/userStatus - 실패")
	void updateUserStatusByUserId_Fail_UserNotFound() throws Exception {
		UUID invalidUserId = UUID.randomUUID();
		UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());

		given(userStatusService.updateByUserId(eq(invalidUserId), any(UserStatusUpdateRequest.class)))
			.willThrow(UserNotFoundException.class);

		mockMvc.perform(patch("/api/users/{userId}/userStatus", invalidUserId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound());
	}

}
