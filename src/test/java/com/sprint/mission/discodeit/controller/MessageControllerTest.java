package com.sprint.mission.discodeit.controller;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;

import jakarta.persistence.EntityNotFoundException;

@ActiveProfiles("test")
@WebMvcTest(MessageController.class)
@Import(com.sprint.mission.discodeit.mapper.PageResponseMapper.class)
public class MessageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private MessageService messageService;

	@Test
	@DisplayName("POST /api/messages - 성공")
	void createMessage_Success() throws Exception {
		UUID channelId = UUID.randomUUID();
		UUID authorId = UUID.randomUUID();
		MessageCreateRequest request = new MessageCreateRequest("안녕하세요", channelId, authorId);

		UserDto mockAuthor = mock(UserDto.class);
		MessageDto expectedResponse = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(), "안녕하세요", channelId, mockAuthor, List.of());
		given(messageService.create(any(MessageCreateRequest.class), any())).willReturn(expectedResponse);

		MockMultipartFile messageRequestPart = new MockMultipartFile(
			"messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
		);
		MockMultipartFile attachmentPart = new MockMultipartFile(
			"attachments", "file.txt", MediaType.TEXT_PLAIN_VALUE, new byte[]{1, 2, 3}
		);

		mockMvc.perform(multipart("/api/messages")
				.file(messageRequestPart)
				.file(attachmentPart))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.content").value("안녕하세요"));
	}

	@Test
	@DisplayName("POST /api/messages - 실패")
	void createMessage_Fail_InvalidRequest() throws Exception {
		MessageCreateRequest request = new MessageCreateRequest("", null, null);

		MockMultipartFile messageRequestPart = new MockMultipartFile(
			"messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
		);

		mockMvc.perform(multipart("/api/messages")
				.file(messageRequestPart))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("PATCH /api/messages/{messageId} - 성공")
	void updateMessage_Success() throws Exception {
		UUID messageId = UUID.randomUUID();
		MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용");

		UserDto mockAuthor = mock(UserDto.class);
		MessageDto expectedResponse = new MessageDto(messageId, Instant.now(), Instant.now(), "수정된 내용", UUID.randomUUID(), mockAuthor, List.of());
		given(messageService.update(eq(messageId), any(MessageUpdateRequest.class))).willReturn(expectedResponse);

		mockMvc.perform(patch("/api/messages/{messageId}", messageId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value("수정된 내용"));
	}

	@Test
	@DisplayName("PATCH /api/messages/{messageId} - 실패")
	void updateMessage_Fail_NotFound() throws Exception {
		UUID invalidMessageId = UUID.randomUUID();
		MessageUpdateRequest request = new MessageUpdateRequest("수정 시도");

		given(messageService.update(eq(invalidMessageId), any(MessageUpdateRequest.class)))
			.willThrow(EntityNotFoundException.class);

		mockMvc.perform(patch("/api/messages/{messageId}", invalidMessageId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("DELETE /api/messages/{messageId} - 성공")
	void deleteMessage_Success() throws Exception {
		UUID messageId = UUID.randomUUID();
		willDoNothing().given(messageService).delete(messageId);

		mockMvc.perform(delete("/api/messages/{messageId}", messageId))
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("DELETE /api/messages/{messageId} - 실패")
	void deleteMessage_Fail_NotFound() throws Exception {
		UUID invalidMessageId = UUID.randomUUID();
		willThrow(EntityNotFoundException.class).given(messageService).delete(invalidMessageId);

		mockMvc.perform(delete("/api/messages/{messageId}", invalidMessageId))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("GET /api/messages - 성공")
	void findAllByChannelId_Success() throws Exception {
		UUID channelId = UUID.randomUUID();
		UserDto mockAuthor = mock(UserDto.class);
		MessageDto message = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(), "메시지", channelId, mockAuthor, List.of());
		PageResponse<MessageDto> expectedResponse = new PageResponse<>(
			List.of(message),
			Instant.now(),
			1,
			false,
			1L
		);
		given(messageService.findAllByChannelId(eq(channelId), any(), any())).willReturn(expectedResponse);

		mockMvc.perform(get("/api/messages")
				.param("channelId", channelId.toString())
				.param("cursor", Instant.now().toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.size()").value(1))
			.andExpect(jsonPath("$.content[0].content").value("메시지"));
	}

	@Test
	@DisplayName("GET /api/messages - 실패")
	void findAllByChannelId_Fail_MissingParam() throws Exception {
		mockMvc.perform(get("/api/messages"))
			.andExpect(status().isBadRequest());
	}
}
