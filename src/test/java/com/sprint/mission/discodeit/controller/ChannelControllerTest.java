package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;

@ActiveProfiles("test")
@WebMvcTest(ChannelController.class)
@Import(com.sprint.mission.discodeit.mapper.PageResponseMapper.class)
public class ChannelControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ChannelService channelService;

	@Test
	@DisplayName("POST /api/channels/public - 성공")
	void createPublicChannel_Success() throws Exception {
		PublicChannelCreateRequest request = new PublicChannelCreateRequest("일반채널", "공개방설명");
		ChannelDto expectedResponse = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "일반채널", "공개방설명", List.of(), Instant.now());

		given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(expectedResponse);

		mockMvc.perform(post("/api/channels/public")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("일반채널"))
			.andExpect(jsonPath("$.type").value("PUBLIC"));
	}

	@Test
	@DisplayName("POST /api/channels/public - 실패")
	void createPublicChannel_Fail_InvalidRequest() throws Exception {
		PublicChannelCreateRequest request = new PublicChannelCreateRequest("", "이름이 비어있음");

		mockMvc.perform(post("/api/channels/public")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/channels/private - 성공")
	void createPrivateChannel_Success() throws Exception {
		PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(UUID.randomUUID()));
		ChannelDto expectedResponse = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, "비밀채널", null, List.of(), Instant.now());;

		given(channelService.create(any(PrivateChannelCreateRequest.class))).willReturn(expectedResponse);

		mockMvc.perform(post("/api/channels/private")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("비밀채널"))
			.andExpect(jsonPath("$.type").value("PRIVATE"));
	}

	@Test
	@DisplayName("POST /api/channels/private - 실패")
	void createPrivateChannel_Fail_InvalidRequest() throws Exception {
		PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(UUID.randomUUID()));

		mockMvc.perform(post("/api/channels/private")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("PATCH /api/channels/{channelId} - 성공")
	void updateChannel_Success() throws Exception {
		UUID channelId = UUID.randomUUID();
		PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("수정된채널", "수정된설명");
		ChannelDto expectedResponse = new ChannelDto(channelId, ChannelType.PUBLIC, "수정된채널", "수정된설명", List.of(), Instant.now());

		given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class))).willReturn(expectedResponse);

		mockMvc.perform(patch("/api/channels/{channelId}", channelId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("수정된채널"))
			.andExpect(jsonPath("$.description").value("수정된설명"));
	}

	@Test
	@DisplayName("PATCH /api/channels/{channelId} - 실패")
	void updateChannel_Fail_NotFound() throws Exception {
		UUID invalidChannelId = UUID.randomUUID();
		PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("유령채널", "수정시도");

		given(channelService.update(eq(invalidChannelId), any(PublicChannelUpdateRequest.class)))
			.willThrow(ChannelNotFoundException.class);

		mockMvc.perform(patch("/api/channels/{channelId}", invalidChannelId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("DELETE /api/channels/{channelId} - 성공")
	void deleteChannel_Success() throws Exception {
		UUID channelId = UUID.randomUUID();
		willDoNothing().given(channelService).delete(channelId);

		mockMvc.perform(delete("/api/channels/{channelId}", channelId))
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("DELETE /api/channels/{channelId} - 실패")
	void deleteChannel_Fail_NotFound() throws Exception {
		UUID invalidChannelId = UUID.randomUUID();
		willThrow(ChannelNotFoundException.class).given(channelService).delete(invalidChannelId);

		mockMvc.perform(delete("/api/channels/{channelId}", invalidChannelId))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("GET /api/channels - 성공")
	void findAll_Success() throws Exception {
		UUID userId = UUID.randomUUID();
		ChannelDto channel1 = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "채널1", "설명1", List.of(), Instant.now());
		ChannelDto channel2 = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, "채널2", "설명2", List.of(), Instant.now());

		given(channelService.findAllByUserId(userId)).willReturn(List.of(channel1, channel2));

		mockMvc.perform(get("/api/channels")
				.param("userId", userId.toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(2))
			.andExpect(jsonPath("$[0].name").value("채널1"))
			.andExpect(jsonPath("$[1].name").value("채널2"));
	}

	@Test
	@DisplayName("GET /api/channels - 실패")
	void findAll_Fail_MissingParam() throws Exception {
		mockMvc.perform(get("/api/channels"))
			.andExpect(status().isBadRequest());
	}
}
