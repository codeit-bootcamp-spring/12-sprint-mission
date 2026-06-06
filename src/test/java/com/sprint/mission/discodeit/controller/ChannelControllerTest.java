package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ChannelService channelService;

  @Test
  @DisplayName("공개 채널 생성 성공")
  void createPublic_success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();

    PublicChannelCreateRequest request = new PublicChannelCreateRequest(
        "공지사항",
        "공지 채널입니다."
    );

    ChannelDto response = new ChannelDto(
        channelId,
        ChannelType.PUBLIC,
        "공지사항",
        "공지 채널입니다.",
        List.of(),
        null
    );

    given(channelService.create(any(PublicChannelCreateRequest.class)))
        .willReturn(response);

    // when & then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.type").value("PUBLIC"))
        .andExpect(jsonPath("$.name").value("공지사항"))
        .andExpect(jsonPath("$.description").value("공지 채널입니다."));

    then(channelService).should()
        .create(any(PublicChannelCreateRequest.class));
  }

  @Test
  @DisplayName("공개 채널 생성 실패 - 요청값 검증 실패")
  void createPublic_fail_validation() throws Exception {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest(
        "",
        "공지 채널입니다."
    );

    // when & then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    then(channelService).should(never())
        .create(any(PublicChannelCreateRequest.class));
  }

  @Test
  @DisplayName("비공개 채널 생성 성공")
  void createPrivate_success() throws Exception {
    // given
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();

    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
        List.of(userId1, userId2)
    );

    ChannelDto response = new ChannelDto(
        channelId,
        ChannelType.PRIVATE,
        null,
        null,
        List.of(),
        null
    );

    given(channelService.create(any(PrivateChannelCreateRequest.class)))
        .willReturn(response);

    // when & then
    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.type").value("PRIVATE"));

    then(channelService).should()
        .create(any(PrivateChannelCreateRequest.class));
  }

  @Test
  @DisplayName("비공개 채널 생성 실패 - 참여자 목록 비어 있음")
  void createPrivate_fail_validation() throws Exception {
    // given
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
        List.of()
    );

    // when & then
    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    then(channelService).should(never())
        .create(any(PrivateChannelCreateRequest.class));
  }

  @Test
  @DisplayName("채널 수정 성공")
  void update_success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();

    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
        "수정된 채널",
        "수정된 설명"
    );

    ChannelDto response = new ChannelDto(
        channelId,
        ChannelType.PUBLIC,
        "수정된 채널",
        "수정된 설명",
        List.of(),
        Instant.now()
    );

    given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class)))
        .willReturn(response);

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.type").value("PUBLIC"))
        .andExpect(jsonPath("$.name").value("수정된 채널"))
        .andExpect(jsonPath("$.description").value("수정된 설명"));

    then(channelService).should()
        .update(eq(channelId), any(PublicChannelUpdateRequest.class));
  }

  @Test
  @DisplayName("채널 수정 실패 - 채널 없음")
  void update_fail_channelNotFound() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();

    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
        "수정된 채널",
        "수정된 설명"
    );

    given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class)))
        .willThrow(new ChannelNotFoundException(channelId));

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());

    then(channelService).should()
        .update(eq(channelId), any(PublicChannelUpdateRequest.class));
  }

  @Test
  @DisplayName("채널 삭제 성공")
  void delete_success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();

    // when & then
    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());

    then(channelService).should()
        .delete(channelId);
  }

  @Test
  @DisplayName("채널 삭제 실패 - 채널 없음")
  void delete_fail_channelNotFound() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();

    org.mockito.BDDMockito.willThrow(new ChannelNotFoundException(channelId))
        .given(channelService)
        .delete(channelId);

    // when & then
    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNotFound());

    then(channelService).should()
        .delete(channelId);
  }

  @Test
  @DisplayName("사용자 ID로 채널 목록 조회 성공")
  void findAll_success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    ChannelDto channel = new ChannelDto(
        UUID.randomUUID(),
        ChannelType.PUBLIC,
        "공지사항",
        "공지 채널입니다.",
        List.of(),
        null
    );

    given(channelService.findAllByUserId(userId))
        .willReturn(List.of(channel));

    // when & then
    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].type").value("PUBLIC"))
        .andExpect(jsonPath("$[0].name").value("공지사항"))
        .andExpect(jsonPath("$[0].description").value("공지 채널입니다."));

    then(channelService).should()
        .findAllByUserId(userId);
  }

  @Test
  @DisplayName("사용자 ID로 채널 목록 조회 실패 - userId 누락")
  void findAll_fail_missingUserId() throws Exception {
    // when & then
    mockMvc.perform(get("/api/channels"))
        .andExpect(status().isBadRequest());

    then(channelService).should(never())
        .findAllByUserId(any(UUID.class));
  }
}