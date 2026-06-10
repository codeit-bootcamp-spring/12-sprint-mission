package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.service.ChannelService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ChannelController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ChannelService channelService;

  @MockitoBean
  private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  // ── createPublic ─────────────────────────────────────────────────────────

  @Test
  @DisplayName("Public 채널 생성 성공 - 201")
  void createPublicChannel() throws Exception {
    UUID channelId = UuidCreator.getTimeOrderedEpoch();
    ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "general", "일반 채널",
        List.of(), Instant.now());
    given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(channelDto);

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(new PublicChannelCreateRequest("general", "일반 채널"))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.name").value("general"))
        .andExpect(jsonPath("$.type").value("PUBLIC"));
  }

  @Test
  @DisplayName("Public 채널 생성 실패 - 400 (채널 이름 없음)")
  void createPublicChannel_blankName() throws Exception {
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new PublicChannelCreateRequest("", "설명"))))
        .andExpect(status().isBadRequest());
  }

  // ── createPrivate ────────────────────────────────────────────────────────

  @Test
  @DisplayName("Private 채널 생성 성공 - 201")
  void createPrivateChannel() throws Exception {
    UUID channelId = UuidCreator.getTimeOrderedEpoch();
    UUID userId = UuidCreator.getTimeOrderedEpoch();
    ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PRIVATE, null, null, List.of(),
        Instant.now());
    given(channelService.create(any(PrivateChannelCreateRequest.class))).willReturn(channelDto);

    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new PrivateChannelCreateRequest(List.of(userId)))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.type").value("PRIVATE"));
  }

  @Test
  @DisplayName("Private 채널 생성 실패 - 400 (참가자 목록 없음)")
  void createPrivateChannel_emptyParticipants() throws Exception {
    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new PrivateChannelCreateRequest(List.of()))))
        .andExpect(status().isBadRequest());
  }

  // ── update ───────────────────────────────────────────────────────────────

  @Test
  @DisplayName("채널 수정 성공 - 200")
  void update() throws Exception {
    UUID channelId = UuidCreator.getTimeOrderedEpoch();
    ChannelDto updatedChannel = new ChannelDto(channelId, ChannelType.PUBLIC, "updated", "수정된 설명",
        List.of(), Instant.now());
    given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class))).willReturn(
        updatedChannel);

    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(new PublicChannelUpdateRequest("updated", "수정된 설명"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("updated"));
  }

  @Test
  @DisplayName("채널 수정 실패 - 400 (Private 채널 수정 시도)")
  void update_privateChannel() throws Exception {
    UUID channelId = UuidCreator.getTimeOrderedEpoch();
    given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class)))
        .willThrow(PrivateChannelUpdateException.withChannelId(channelId));

    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new PublicChannelUpdateRequest("updated", null))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("PRIVATE_CHANNEL_UPDATE"));
  }

  // ── delete ───────────────────────────────────────────────────────────────

  @Test
  @DisplayName("채널 삭제 성공 - 204")
  void deleteChannel() throws Exception {
    UUID channelId = UuidCreator.getTimeOrderedEpoch();
    willDoNothing().given(channelService).delete(channelId);

    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("채널 삭제 실패 - 404 (존재하지 않는 채널)")
  void delete_notFound() throws Exception {
    UUID channelId = UuidCreator.getTimeOrderedEpoch();
    willThrow(ChannelNotFoundException.withId(channelId)).given(channelService).delete(channelId);

    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
  }

  // ── find ─────────────────────────────────────────────────────────────────

  @Test
  @DisplayName("사용자의 채널 목록 조회 성공 - 200")
  void findAllByUserId() throws Exception {
    UUID userId = UuidCreator.getTimeOrderedEpoch();
    UUID channelId = UuidCreator.getTimeOrderedEpoch();
    ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "general", null,
        List.of(), Instant.now());
    given(channelService.findAllByUserId(userId)).willReturn(List.of(channelDto));

    mockMvc.perform(get("/api/channels").param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(channelId.toString()))
        .andExpect(jsonPath("$[0].name").value("general"));
  }

  @Test
  @DisplayName("사용자의 채널 목록 조회 - 빈 목록 반환")
  void findAllByUserId_emptyList() throws Exception {
    UUID userId = UuidCreator.getTimeOrderedEpoch();
    given(channelService.findAllByUserId(userId)).willReturn(List.of());

    mockMvc.perform(get("/api/channels").param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }
}
