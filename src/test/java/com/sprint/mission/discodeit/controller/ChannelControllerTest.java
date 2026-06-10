package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
@Import(GlobalExceptionHandler.class)
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ChannelService channelService;

  private UUID channelId;
  private UUID userId;
  private ChannelDto channelDto;

  @BeforeEach
  void setUp() {
    channelId = UUID.randomUUID();
    userId = UUID.randomUUID();
    channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "공지사항", "공지 채널", List.of(), null);
  }

  // ── createPublic ─────────────────────────────────────────

  @Test
  @DisplayName("PUBLIC 채널 생성 성공")
  void createPublicChannel_Success() throws Exception {
    // given
    given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(channelDto);

    // when & then
    mockMvc.perform(post("/api/channels/createPublic")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelCreateRequest("공지사항", "공지 채널"))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("공지사항"))
        .andExpect(jsonPath("$.type").value("PUBLIC"));
  }

  @Test
  @DisplayName("PUBLIC 채널 생성 시 이름 누락이면 400 반환")
  void createPublicChannel_InvalidRequest_Returns400() throws Exception {
    // when & then
    mockMvc.perform(post("/api/channels/createPublic")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelCreateRequest("", "공지 채널"))))
        .andExpect(status().isBadRequest());
  }

  // ── createPrivate ────────────────────────────────────────

  @Test
  @DisplayName("PRIVATE 채널 생성 성공")
  void createPrivateChannel_Success() throws Exception {
    // given
    ChannelDto privateDto = new ChannelDto(channelId, ChannelType.PRIVATE, null, null, List.of(),
        null);
    given(channelService.create(any(PrivateChannelCreateRequest.class))).willReturn(privateDto);

    // when & then
    mockMvc.perform(post("/api/channels/createPrivate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PrivateChannelCreateRequest(List.of(userId)))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value("PRIVATE"));
  }

  @Test
  @DisplayName("PRIVATE 채널 생성 시 참가자 없으면 400 반환")
  void createPrivateChannel_EmptyParticipants_Returns400() throws Exception {
    // when & then
    mockMvc.perform(post("/api/channels/createPrivate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PrivateChannelCreateRequest(List.of()))))
        .andExpect(status().isBadRequest());
  }

  // ── update ──────────────────────────────────────────────

  @Test
  @DisplayName("채널 수정 성공")
  void updateChannel_Success() throws Exception {
    // given
    given(channelService.update(eq(channelId), any())).willReturn(channelDto);

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelUpdateRequest("새이름", "새설명"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("공지사항"));
  }

  @Test
  @DisplayName("존재하지 않는 채널 수정 시 404 반환")
  void updateChannel_NotFound_Returns404() throws Exception {
    // given
    given(channelService.update(eq(channelId), any()))
        .willThrow(new ChannelNotFoundException());

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelUpdateRequest("새이름", "새설명"))))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("PRIVATE 채널 수정 시도 시 400 반환")
  void updateChannel_PrivateChannel_Returns400() throws Exception {
    // given
    given(channelService.update(eq(channelId), any()))
        .willThrow(new PrivateChannelUpdateException());

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelUpdateRequest("새이름", "새설명"))))
        .andExpect(status().isBadRequest());
  }

  // ── delete ──────────────────────────────────────────────

  @Test
  @DisplayName("채널 삭제 성공")
  void deleteChannel_Success() throws Exception {
    // given
    doNothing().when(channelService).delete(eq(channelId));

    // when & then
    mockMvc.perform(delete("/api/channels/{channelId}", channelId)
            .param("channelId", channelId.toString()))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("존재하지 않는 채널 삭제 시 404 반환")
  void deleteChannel_NotFound_Returns404() throws Exception {
    // given
    doThrow(new ChannelNotFoundException()).when(channelService).delete(eq(channelId));

    // when & then
    mockMvc.perform(delete("/api/channels/{channelId}", channelId)
            .param("channelId", channelId.toString()))
        .andExpect(status().isNotFound());
  }

  // ── findAll ──────────────────────────────────────────────

  @Test
  @DisplayName("userId로 채널 목록 조회 성공")
  void findAll_Success() throws Exception {
    // given
    given(channelService.findAllByUserId(eq(userId))).willReturn(List.of(channelDto));

    // when & then
    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].name").value("공지사항"));
  }

  @Test
  @DisplayName("채널 없으면 빈 목록 반환")
  void findAll_Empty_ReturnsEmptyList() throws Exception {
    // given
    given(channelService.findAllByUserId(eq(userId))).willReturn(List.of());

    // when & then
    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }
}