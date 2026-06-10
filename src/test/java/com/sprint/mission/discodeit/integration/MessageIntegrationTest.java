package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MessageIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserService userService;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private MessageService messageService;

  // ── create ──────────────────────────────────────────────

  @Test
  @DisplayName("메시지 생성 성공")
  void createMessage_Success() throws Exception {
    // given
    UserDto user = userService.create(
        new UserCreateRequest("msgUser", "msg@example.com", "password123"), Optional.empty());
    ChannelDto channel = channelService.create(new PublicChannelCreateRequest("테스트채널", null));

    MessageCreateRequest request = new MessageCreateRequest("안녕하세요", channel.id(), user.id());
    MockMultipartFile messagePart = new MockMultipartFile(
        "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request));

    // when & then
    mockMvc.perform(multipart("/api/messages").file(messagePart))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.content", is("안녕하세요")))
        .andExpect(jsonPath("$.channelId", is(channel.id().toString())));
  }

  @Test
  @DisplayName("존재하지 않는 채널에 메시지 생성 시 404 반환")
  void createMessage_ChannelNotFound_Returns404() throws Exception {
    // given
    UserDto user = userService.create(
        new UserCreateRequest("msgUser2", "msg2@example.com", "password123"), Optional.empty());

    MessageCreateRequest request = new MessageCreateRequest("안녕", UUID.randomUUID(), user.id());
    MockMultipartFile messagePart = new MockMultipartFile(
        "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request));

    // when & then
    mockMvc.perform(multipart("/api/messages").file(messagePart))
        .andExpect(status().isNotFound());
  }

  // ── update ──────────────────────────────────────────────

  @Test
  @DisplayName("메시지 수정 성공")
  void updateMessage_Success() throws Exception {
    // given
    UserDto user = userService.create(
        new UserCreateRequest("updateUser", "update@example.com", "password123"), Optional.empty());
    ChannelDto channel = channelService.create(new PublicChannelCreateRequest("수정채널", null));
    MessageDto message = messageService.create(
        new MessageCreateRequest("원래 내용", channel.id(), user.id()), List.of());

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", message.id())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new MessageUpdateRequest("수정된 내용"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(message.id().toString())))
        .andExpect(jsonPath("$.content", is("수정된 내용")));
  }

  @Test
  @DisplayName("존재하지 않는 메시지 수정 시 404 반환")
  void updateMessage_NotFound_Returns404() throws Exception {
    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new MessageUpdateRequest("수정 내용"))))
        .andExpect(status().isNotFound());
  }

  // ── delete ──────────────────────────────────────────────

  @Test
  @DisplayName("메시지 삭제 성공")
  void deleteMessage_Success() throws Exception {
    // given
    UserDto user = userService.create(
        new UserCreateRequest("deleteUser", "delete@example.com", "password123"), Optional.empty());
    ChannelDto channel = channelService.create(new PublicChannelCreateRequest("삭제채널", null));
    MessageDto message = messageService.create(
        new MessageCreateRequest("삭제할 메시지", channel.id(), user.id()), List.of());

    // when & then
    mockMvc.perform(delete("/api/messages/{messageId}", message.id()))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("존재하지 않는 메시지 삭제 시 404 반환")
  void deleteMessage_NotFound_Returns404() throws Exception {
    // when & then
    mockMvc.perform(delete("/api/messages/{messageId}", UUID.randomUUID()))
        .andExpect(status().isNotFound());
  }

  // ── findAllByChannelId ───────────────────────────────────

  @Test
  @DisplayName("채널 메시지 목록 조회 성공")
  void findAllByChannelId_Success() throws Exception {
    // given
    UserDto user = userService.create(
        new UserCreateRequest("listUser", "list@example.com", "password123"), Optional.empty());
    ChannelDto channel = channelService.create(new PublicChannelCreateRequest("목록채널", null));
    messageService.create(new MessageCreateRequest("첫번째", channel.id(), user.id()), List.of());
    messageService.create(new MessageCreateRequest("두번째", channel.id(), user.id()), List.of());

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channel.id().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)));
  }

  @Test
  @DisplayName("메시지 없는 채널 조회 시 빈 목록 반환")
  void findAllByChannelId_Empty_ReturnsEmpty() throws Exception {
    // given
    ChannelDto channel = channelService.create(new PublicChannelCreateRequest("빈채널", null));

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channel.id().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(0)));
  }
}