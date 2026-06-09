package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MessageService messageService;

  @Test
  @DisplayName("메시지 생성 성공")
  void create_success() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    MessageCreateRequest request = new MessageCreateRequest(
        "안녕하세요",
        channelId,
        authorId
    );

    UserDto author = new UserDto(
        authorId,
        "minji",
        "minji@test.com",
        null,
        true
    );

    MessageDto response = new MessageDto(
        messageId,
        Instant.now(),
        Instant.now(),
        "안녕하세요",
        channelId,
        author,
        List.of()
    );

    given(messageService.create(any(MessageCreateRequest.class), anyList()))
        .willReturn(response);

    MockMultipartFile messageCreateRequest = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(messageCreateRequest))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.content").value("안녕하세요"))
        .andExpect(jsonPath("$.channelId").value(channelId.toString()))
        .andExpect(jsonPath("$.author.username").value("minji"));

    then(messageService).should()
        .create(any(MessageCreateRequest.class), anyList());
  }

  @Test
  @DisplayName("메시지 생성 실패 - 요청값 검증 실패")
  void create_fail_validation() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    MessageCreateRequest request = new MessageCreateRequest(
        "",
        channelId,
        authorId
    );

    MockMultipartFile messageCreateRequest = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(messageCreateRequest))
        .andExpect(status().isBadRequest());

    then(messageService).should(never())
        .create(any(MessageCreateRequest.class), anyList());
  }

  @Test
  @DisplayName("메시지 수정 성공")
  void update_success() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    MessageUpdateRequest request = new MessageUpdateRequest("수정된 메시지");

    UserDto author = new UserDto(
        authorId,
        "minji",
        "minji@test.com",
        null,
        true
    );

    MessageDto response = new MessageDto(
        messageId,
        Instant.now(),
        Instant.now(),
        "수정된 메시지",
        channelId,
        author,
        List.of()
    );

    given(messageService.update(eq(messageId), any(MessageUpdateRequest.class)))
        .willReturn(response);

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.content").value("수정된 메시지"))
        .andExpect(jsonPath("$.author.username").value("minji"));

    then(messageService).should()
        .update(eq(messageId), any(MessageUpdateRequest.class));
  }

  @Test
  @DisplayName("메시지 수정 실패 - 메시지 없음")
  void update_fail_messageNotFound() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();

    MessageUpdateRequest request = new MessageUpdateRequest("수정된 메시지");

    given(messageService.update(eq(messageId), any(MessageUpdateRequest.class)))
        .willThrow(new MessageNotFoundException(messageId));

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());

    then(messageService).should()
        .update(eq(messageId), any(MessageUpdateRequest.class));
  }

  @Test
  @DisplayName("메시지 삭제 성공")
  void delete_success() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();

    // when & then
    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent());

    then(messageService).should()
        .delete(messageId);
  }

  @Test
  @DisplayName("메시지 삭제 실패 - 메시지 없음")
  void delete_fail_messageNotFound() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();

    org.mockito.BDDMockito.willThrow(new MessageNotFoundException(messageId))
        .given(messageService)
        .delete(messageId);

    // when & then
    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNotFound());

    then(messageService).should()
        .delete(messageId);
  }

  @Test
  @DisplayName("채널 ID로 메시지 목록 조회 성공")
  void findAllByChannelId_success() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    Instant cursor = Instant.now();

    UserDto author = new UserDto(
        authorId,
        "minji",
        "minji@test.com",
        null,
        true
    );

    MessageDto message = new MessageDto(
        messageId,
        Instant.now(),
        Instant.now(),
        "메시지",
        channelId,
        author,
        List.of()
    );

    PageResponse<MessageDto> response = new PageResponse<>(
        List.of(message),
        cursor,
        1,
        false,
        1L
    );

    given(messageService.findAllByChannelId(
        eq(channelId),
        any(Instant.class),
        any(Pageable.class)
    )).willReturn(response);

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString())
            .param("cursor", cursor.toString())
            .param("size", "10")
            .param("sort", "createdAt,desc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(messageId.toString()))
        .andExpect(jsonPath("$.content[0].content").value("메시지"))
        .andExpect(jsonPath("$.content[0].channelId").value(channelId.toString()))
        .andExpect(jsonPath("$.size").value(1))
        .andExpect(jsonPath("$.hasNext").value(false))
        .andExpect(jsonPath("$.totalElements").value(1));

    then(messageService).should()
        .findAllByChannelId(eq(channelId), any(Instant.class), any(Pageable.class));
  }

  @Test
  @DisplayName("채널 ID로 메시지 목록 조회 실패 - channelId 누락")
  void findAllByChannelId_fail_missingChannelId() throws Exception {
    // when & then
    mockMvc.perform(get("/api/messages"))
        .andExpect(status().isBadRequest());

    then(messageService).should(never())
        .findAllByChannelId(any(UUID.class), any(Instant.class), any(Pageable.class));
  }
}