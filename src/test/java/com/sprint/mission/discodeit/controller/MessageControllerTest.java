package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
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
  @MockitoBean
  private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  @DisplayName("메시지를 생성할 수 있다")
  void createMessage_success() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    Instant createdAt = Instant.parse("2026-06-04T00:00:00Z");
    Instant updatedAt = Instant.parse("2026-06-04T00:00:00Z");

    MessageCreateRequest request = new MessageCreateRequest(
        "hello message",
        channelId,
        authorId
    );

    UserResponse author = new UserResponse(
        authorId,
        "testUser",
        "test@test.com",
        null,
        true
    );

    MessageResponse response = new MessageResponse(
        messageId,
        createdAt,
        updatedAt,
        "hello message",
        channelId,
        author,
        List.of()
    );

    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    given(messageService.create(any(MessageCreateRequest.class), any()))
        .willReturn(response);

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.createdAt").value("2026-06-04T00:00:00Z"))
        .andExpect(jsonPath("$.updatedAt").value("2026-06-04T00:00:00Z"))
        .andExpect(jsonPath("$.content").value("hello message"))
        .andExpect(jsonPath("$.channelId").value(channelId.toString()))
        .andExpect(jsonPath("$.author.id").value(authorId.toString()))
        .andExpect(jsonPath("$.author.username").value("testUser"))
        .andExpect(jsonPath("$.author.email").value("test@test.com"))
        .andExpect(jsonPath("$.author.online").value(true))
        .andExpect(jsonPath("$.attachments").isArray())
        .andExpect(jsonPath("$.attachments").isEmpty());

    verify(messageService).create(any(MessageCreateRequest.class), any());
  }

  @Test
  @DisplayName("첨부파일과 함께 메시지를 생성할 수 있다")
  void createMessage_withAttachments_success() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    Instant createdAt = Instant.parse("2026-06-04T00:00:00Z");
    Instant updatedAt = Instant.parse("2026-06-04T00:00:00Z");

    MessageCreateRequest request = new MessageCreateRequest(
        "message with file",
        channelId,
        authorId
    );

    UserResponse author = new UserResponse(
        authorId,
        "testUser",
        "test@test.com",
        null,
        true
    );

    MessageResponse response = new MessageResponse(
        messageId,
        createdAt,
        updatedAt,
        "message with file",
        channelId,
        author,
        List.of()
    );

    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    MockMultipartFile attachment1 = new MockMultipartFile(
        "attachments",
        "test1.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "file-content-1".getBytes()
    );

    MockMultipartFile attachment2 = new MockMultipartFile(
        "attachments",
        "test2.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "file-content-2".getBytes()
    );

    given(messageService.create(any(MessageCreateRequest.class), any()))
        .willReturn(response);

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(requestPart)
            .file(attachment1)
            .file(attachment2)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.content").value("message with file"))
        .andExpect(jsonPath("$.channelId").value(channelId.toString()))
        .andExpect(jsonPath("$.author.id").value(authorId.toString()));

    verify(messageService).create(any(MessageCreateRequest.class), any());
  }

  @Test
  @DisplayName("메시지를 수정할 수 있다")
  void updateMessage_success() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    Instant createdAt = Instant.parse("2026-06-04T00:00:00Z");
    Instant updatedAt = Instant.parse("2026-06-04T01:00:00Z");

    MessageUpdateRequest request = new MessageUpdateRequest(
        "updated message"
    );

    UserResponse author = new UserResponse(
        authorId,
        "testUser",
        "test@test.com",
        null,
        true
    );

    MessageResponse response = new MessageResponse(
        messageId,
        createdAt,
        updatedAt,
        "updated message",
        channelId,
        author,
        List.of()
    );

    given(messageService.update(eq(messageId), any(MessageUpdateRequest.class)))
        .willReturn(response);

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.createdAt").value("2026-06-04T00:00:00Z"))
        .andExpect(jsonPath("$.updatedAt").value("2026-06-04T01:00:00Z"))
        .andExpect(jsonPath("$.content").value("updated message"))
        .andExpect(jsonPath("$.channelId").value(channelId.toString()))
        .andExpect(jsonPath("$.author.id").value(authorId.toString()))
        .andExpect(jsonPath("$.attachments").isArray())
        .andExpect(jsonPath("$.attachments").isEmpty());

    verify(messageService).update(eq(messageId), any(MessageUpdateRequest.class));
  }

  @Test
  @DisplayName("존재하지 않는 메시지 수정 요청이면 실패한다")
  void updateMessage_fail_messageNotFound() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();

    MessageUpdateRequest request = new MessageUpdateRequest(
        "updated message"
    );

    given(messageService.update(eq(messageId), any(MessageUpdateRequest.class)))
        .willThrow(new MessageNotFoundException(messageId));

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("메시지를 삭제할 수 있다")
  void deleteMessage_success() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();

    doNothing()
        .when(messageService)
        .delete(messageId);

    // when & then
    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent());

    verify(messageService).delete(messageId);
  }

  @Test
  @DisplayName("존재하지 않는 메시지 삭제 요청이면 실패한다")
  void deleteMessage_fail_messageNotFound() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();

    doThrow(new MessageNotFoundException(messageId))
        .when(messageService)
        .delete(messageId);

    // when & then
    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("채널 ID로 메시지 목록을 페이지 조회할 수 있다")
  void findMessagesByChannelId_success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    UUID messageId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    Instant createdAt = Instant.parse("2026-06-04T00:00:00Z");
    Instant updatedAt = Instant.parse("2026-06-04T00:00:00Z");

    UserResponse author = new UserResponse(
        authorId,
        "testUser",
        "test@test.com",
        null,
        true
    );

    MessageResponse messageResponse = new MessageResponse(
        messageId,
        createdAt,
        updatedAt,
        "hello message",
        channelId,
        author,
        List.of()
    );

    PageResponse<MessageResponse> response = new PageResponse<>(
        List.of(messageResponse),
        0,
        1,
        false,
        null
    );

    given(messageService.findAllByChannelId(channelId, 0))
        .willReturn(response);

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString())
            .param("page", "0"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].id").value(messageId.toString()))
        .andExpect(jsonPath("$.content[0].content").value("hello message"))
        .andExpect(jsonPath("$.content[0].channelId").value(channelId.toString()))
        .andExpect(jsonPath("$.content[0].author.id").value(authorId.toString()))
        .andExpect(jsonPath("$.content[0].author.username").value("testUser"))
        .andExpect(jsonPath("$.number").value(0))
        .andExpect(jsonPath("$.size").value(1))
        .andExpect(jsonPath("$.hasNext").value(false))
        .andExpect(jsonPath("$.totalElements").doesNotExist());

    verify(messageService).findAllByChannelId(channelId, 0);
  }

  @Test
  @DisplayName("채널에 메시지가 없으면 빈 페이지를 반환한다")
  void findMessagesByChannelId_empty() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();

    PageResponse<MessageResponse> response = new PageResponse<>(
        List.of(),
        0,
        0,
        false,
        null
    );

    given(messageService.findAllByChannelId(channelId, 0))
        .willReturn(response);

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content").isEmpty());

    verify(messageService).findAllByChannelId(channelId, 0);
  }

}