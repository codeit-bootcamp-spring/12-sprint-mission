package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.ChannelPrivateCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelPublicCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
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
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
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
  @MockitoBean
  private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  @DisplayName("공개 채널을 생성할 수 있다")
  void createPublicChannel_success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    Instant lastMessageAt = Instant.parse("2026-06-04T00:00:00Z");

    ChannelPublicCreateRequest request = new ChannelPublicCreateRequest(
        "public-channel",
        "public description"
    );

    ChannelResponse response = new ChannelResponse(
        channelId,
        ChannelType.PUBLIC,
        "public-channel",
        "public description",
        List.of(),
        lastMessageAt
    );

    given(channelService.createPublic(any(ChannelPublicCreateRequest.class)))
        .willReturn(response);

    // when & then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.type").value("PUBLIC"))
        .andExpect(jsonPath("$.name").value("public-channel"))
        .andExpect(jsonPath("$.description").value("public description"))
        .andExpect(jsonPath("$.participants").isArray())
        .andExpect(jsonPath("$.participants").isEmpty())
        .andExpect(jsonPath("$.lastMessageAt").value("2026-06-04T00:00:00Z"));

    verify(channelService).createPublic(any(ChannelPublicCreateRequest.class));
  }

  @Test
  @DisplayName("비공개 채널을 생성할 수 있다")
  void createPrivateChannel_success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();
    Instant lastMessageAt = Instant.parse("2026-06-04T00:00:00Z");

    ChannelPrivateCreateRequest request = new ChannelPrivateCreateRequest(
        "private-channel",
        List.of(userId1, userId2)
    );

    List<UserResponse> participants = List.of(
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

    ChannelResponse response = new ChannelResponse(
        channelId,
        ChannelType.PRIVATE,
        "private-channel",
        null,
        participants,
        lastMessageAt
    );

    given(channelService.createPrivate(any(ChannelPrivateCreateRequest.class)))
        .willReturn(response);

    // when & then
    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.type").value("PRIVATE"))
        .andExpect(jsonPath("$.name").value("private-channel"))
        .andExpect(jsonPath("$.participants").isArray())
        .andExpect(jsonPath("$.participants[0].id").value(userId1.toString()))
        .andExpect(jsonPath("$.participants[0].username").value("user1"))
        .andExpect(jsonPath("$.participants[0].email").value("user1@test.com"))
        .andExpect(jsonPath("$.participants[0].online").value(true))
        .andExpect(jsonPath("$.participants[1].id").value(userId2.toString()))
        .andExpect(jsonPath("$.participants[1].username").value("user2"))
        .andExpect(jsonPath("$.participants[1].email").value("user2@test.com"))
        .andExpect(jsonPath("$.participants[1].online").value(false))
        .andExpect(jsonPath("$.lastMessageAt").value("2026-06-04T00:00:00Z"));

    verify(channelService).createPrivate(any(ChannelPrivateCreateRequest.class));
  }

  @Test
  @DisplayName("비공개 채널 이름이 비어 있으면 생성에 실패한다")
  void createPrivateChannel_fail_blankName() throws Exception {
    // given
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();

    ChannelPrivateCreateRequest request = new ChannelPrivateCreateRequest(
        "",
        List.of(userId1, userId2)
    );

    // when & then
    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isBadRequest());

    verify(channelService, never()).createPrivate(any(ChannelPrivateCreateRequest.class));
  }

  @Test
  @DisplayName("채널 정보를 수정할 수 있다")
  void updateChannel_success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    Instant lastMessageAt = Instant.parse("2026-06-04T00:00:00Z");

    ChannelUpdateRequest request = new ChannelUpdateRequest(
        "updated-channel",
        "updated description"
    );

    ChannelResponse response = new ChannelResponse(
        channelId,
        ChannelType.PUBLIC,
        "updated-channel",
        "updated description",
        List.of(),
        lastMessageAt
    );

    given(channelService.update(eq(channelId), any(ChannelUpdateRequest.class)))
        .willReturn(response);

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.type").value("PUBLIC"))
        .andExpect(jsonPath("$.name").value("updated-channel"))
        .andExpect(jsonPath("$.description").value("updated description"))
        .andExpect(jsonPath("$.participants").isArray())
        .andExpect(jsonPath("$.participants").isEmpty())
        .andExpect(jsonPath("$.lastMessageAt").value("2026-06-04T00:00:00Z"));

    verify(channelService).update(eq(channelId), any(ChannelUpdateRequest.class));
  }

  @Test
  @DisplayName("존재하지 않는 채널 수정 요청이면 실패한다")
  void updateChannel_fail_channelNotFound() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();

    ChannelUpdateRequest request = new ChannelUpdateRequest(
        "updated-channel",
        "updated description"
    );

    given(channelService.update(eq(channelId), any(ChannelUpdateRequest.class)))
        .willThrow(new ChannelNotFoundException(channelId));

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("채널을 삭제할 수 있다")
  void deleteChannel_success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();

    doNothing()
        .when(channelService)
        .delete(channelId);

    // when & then
    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());

    verify(channelService).delete(channelId);
  }

  @Test
  @DisplayName("존재하지 않는 채널 삭제 요청이면 실패한다")
  void deleteChannel_fail_channelNotFound() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();

    doThrow(new ChannelNotFoundException(channelId))
        .when(channelService)
        .delete(channelId);

    // when & then
    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("사용자 ID로 채널 목록을 조회할 수 있다")
  void findAllByUserId_success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    UUID channelId1 = UUID.randomUUID();
    UUID channelId2 = UUID.randomUUID();

    Instant publicChannelLastMessageAt = Instant.parse("2026-06-04T00:00:00Z");
    Instant privateChannelLastMessageAt = Instant.parse("2026-06-04T01:00:00Z");

    UserResponse participant = new UserResponse(
        userId,
        "testUser",
        "test@test.com",
        null,
        true
    );

    List<ChannelResponse> responses = List.of(
        new ChannelResponse(
            channelId1,
            ChannelType.PUBLIC,
            "public-channel",
            "public description",
            List.of(),
            publicChannelLastMessageAt
        ),
        new ChannelResponse(
            channelId2,
            ChannelType.PRIVATE,
            null,
            null,
            List.of(participant),
            privateChannelLastMessageAt
        )
    );

    given(channelService.findAllByUserId(userId))
        .willReturn(responses);

    // when & then
    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(channelId1.toString()))
        .andExpect(jsonPath("$[0].type").value("PUBLIC"))
        .andExpect(jsonPath("$[0].name").value("public-channel"))
        .andExpect(jsonPath("$[0].description").value("public description"))
        .andExpect(jsonPath("$[0].participants").isArray())
        .andExpect(jsonPath("$[0].participants").isEmpty())
        .andExpect(jsonPath("$[0].lastMessageAt").value("2026-06-04T00:00:00Z"))
        .andExpect(jsonPath("$[1].id").value(channelId2.toString()))
        .andExpect(jsonPath("$[1].type").value("PRIVATE"))
        .andExpect(jsonPath("$[1].name").doesNotExist())
        .andExpect(jsonPath("$[1].description").doesNotExist())
        .andExpect(jsonPath("$[1].participants[0].id").value(userId.toString()))
        .andExpect(jsonPath("$[1].participants[0].username").value("testUser"))
        .andExpect(jsonPath("$[1].participants[0].email").value("test@test.com"))
        .andExpect(jsonPath("$[1].participants[0].online").value(true))
        .andExpect(jsonPath("$[1].lastMessageAt").value("2026-06-04T01:00:00Z"));

    verify(channelService).findAllByUserId(userId);
  }

  @Test
  @DisplayName("사용자가 참여한 채널이 없으면 빈 목록을 반환한다")
  void findAllByUserId_empty() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    given(channelService.findAllByUserId(userId))
        .willReturn(List.of());

    // when & then
    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());

    verify(channelService).findAllByUserId(userId);
  }

}