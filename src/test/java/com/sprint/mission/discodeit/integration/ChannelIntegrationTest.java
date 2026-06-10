package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ChannelIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private UserService userService;

  // ── createPublic ─────────────────────────────────────────

  @Test
  @DisplayName("PUBLIC 채널 생성 성공")
  void createPublicChannel_Success() throws Exception {
    // when & then
    mockMvc.perform(post("/api/channels/createPublic")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelCreateRequest("공지채널", "공지용"))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.name", is("공지채널")))
        .andExpect(jsonPath("$.type", is("PUBLIC")));
  }

  @Test
  @DisplayName("이름 누락으로 PUBLIC 채널 생성 시 400 반환")
  void createPublicChannel_MissingName_Returns400() throws Exception {
    // when & then
    mockMvc.perform(post("/api/channels/createPublic")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelCreateRequest("", "설명"))))
        .andExpect(status().isBadRequest());
  }

  // ── createPrivate ────────────────────────────────────────

  @Test
  @DisplayName("PRIVATE 채널 생성 성공")
  void createPrivateChannel_Success() throws Exception {
    // given
    UserDto user = userService.create(
        new UserCreateRequest("privateUser", "private@example.com", "password123"),
        Optional.empty());

    // when & then
    mockMvc.perform(post("/api/channels/createPrivate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PrivateChannelCreateRequest(List.of(user.id())))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.type", is("PRIVATE")));
  }

  // ── update ──────────────────────────────────────────────

  @Test
  @DisplayName("채널 수정 성공")
  void updateChannel_Success() throws Exception {
    // given
    ChannelDto created = channelService.create(new PublicChannelCreateRequest("원래이름", "원래설명"));
    UUID channelId = created.id();

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelUpdateRequest("새이름", "새설명"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(channelId.toString())))
        .andExpect(jsonPath("$.name", is("새이름")));
  }

  @Test
  @DisplayName("존재하지 않는 채널 수정 시 404 반환")
  void updateChannel_NotFound_Returns404() throws Exception {
    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelUpdateRequest("새이름", "새설명"))))
        .andExpect(status().isNotFound());
  }

  // ── delete ──────────────────────────────────────────────

  @Test
  @DisplayName("채널 삭제 성공")
  void deleteChannel_Success() throws Exception {
    // given
    ChannelDto created = channelService.create(new PublicChannelCreateRequest("삭제채널", "삭제용"));
    UUID channelId = created.id();

    // when & then
    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("존재하지 않는 채널 삭제 시 404 반환")
  void deleteChannel_NotFound_Returns404() throws Exception {
    // when & then
    mockMvc.perform(delete("/api/channels/{channelId}", UUID.randomUUID()))
        .andExpect(status().isNotFound());
  }

  // ── findAll ──────────────────────────────────────────────

  @Test
  @DisplayName("userId로 채널 목록 조회 성공")
  void findAllChannels_Success() throws Exception {
    // given
    UserDto user = userService.create(
        new UserCreateRequest("findUser", "find@example.com", "password123"),
        Optional.empty());

    channelService.create(new PublicChannelCreateRequest("채널1", "설명1"));
    channelService.create(new PublicChannelCreateRequest("채널2", "설명2"));

    // when & then
    mockMvc.perform(get("/api/channels")
            .param("userId", user.id().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }
}