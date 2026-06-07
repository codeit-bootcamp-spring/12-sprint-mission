package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
@Import(GlobalExceptionHandler.class)
class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MessageService messageService;

  private UUID messageId;
  private UUID channelId;
  private UUID authorId;
  private MessageDto messageDto;

  @BeforeEach
  void setUp() {
    messageId = UUID.randomUUID();
    channelId = UUID.randomUUID();
    authorId = UUID.randomUUID();
    messageDto = new MessageDto(messageId, Instant.now(), Instant.now(), "안녕하세요", channelId, null,
        List.of());
  }

  // ── create ──────────────────────────────────────────────

  @Test
  @DisplayName("메시지 생성 성공")
  void createMessage_Success() throws Exception {
    // given
    given(messageService.create(any(), any())).willReturn(messageDto);

    MockMultipartFile messageCreateRequest = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new MessageCreateRequest("안녕하세요", channelId, authorId))
    );

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(messageCreateRequest))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("안녕하세요"));
  }

  @Test
  @DisplayName("메시지 생성 시 내용 누락이면 400 반환")
  void createMessage_InvalidRequest_Returns400() throws Exception {
    // given
    MockMultipartFile messageCreateRequest = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new MessageCreateRequest("", channelId, authorId))
    );

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(messageCreateRequest))
        .andExpect(status().isBadRequest());
  }

  // ── update ──────────────────────────────────────────────

  @Test
  @DisplayName("메시지 수정 성공")
  void updateMessage_Success() throws Exception {
    // given
    given(messageService.update(eq(messageId), any())).willReturn(messageDto);

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new MessageUpdateRequest("수정된 내용"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("안녕하세요"));
  }

  @Test
  @DisplayName("존재하지 않는 메시지 수정 시 404 반환")
  void updateMessage_NotFound_Returns404() throws Exception {
    // given
    given(messageService.update(eq(messageId), any()))
        .willThrow(new MessageNotFoundException());

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new MessageUpdateRequest("수정된 내용"))))
        .andExpect(status().isNotFound());
  }

  // ── delete ──────────────────────────────────────────────

  @Test
  @DisplayName("메시지 삭제 성공")
  void deleteMessage_Success() throws Exception {
    // given
    doNothing().when(messageService).delete(eq(messageId));

    // when & then
    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("존재하지 않는 메시지 삭제 시 404 반환")
  void deleteMessage_NotFound_Returns404() throws Exception {
    // given
    doThrow(new MessageNotFoundException()).when(messageService).delete(eq(messageId));

    // when & then
    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNotFound());
  }

  // ── findAllByChannelId ───────────────────────────────────

  @Test
  @DisplayName("채널 메시지 목록 조회 성공")
  void findAllByChannelId_Success() throws Exception {
    // given
    PageResponse<MessageDto> pageResponse = new PageResponse<>(
        List.of(messageDto), null, 1, false, null);
    given(messageService.findAllByChannelId(eq(channelId), any(), any()))
        .willReturn(pageResponse);

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].content").value("안녕하세요"));
  }

  @Test
  @DisplayName("메시지 없는 채널 조회 시 빈 목록 반환")
  void findAllByChannelId_Empty_ReturnsEmpty() throws Exception {
    // given
    PageResponse<MessageDto> pageResponse = new PageResponse<>(
        List.of(), null, 0, false, null);
    given(messageService.findAllByChannelId(eq(channelId), any(), any()))
        .willReturn(pageResponse);

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isEmpty());
  }
}