package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.MDCLoggingInterceptor;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
@Import({GlobalExceptionHandler.class, MDCLoggingInterceptor.class})
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
    @DisplayName("메시지 생성 성공")
    void create_success() throws Exception {
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID attachmentId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest(
                "hello",
                channelId,
                authorId
        );

        UserResponse author = new UserResponse(
                authorId,
                "user1",
                "user1@test.com",
                null,
                true
        );

        BinaryContentResponse attachmentResponse = new BinaryContentResponse(
                attachmentId,
                "test.txt",
                5L,
                "text/plain"
        );

        MessageResponse response = new MessageResponse(
                messageId,
                Instant.now(),
                Instant.now(),
                "hello",
                channelId,
                author,
                List.of(attachmentResponse)
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "messageCreateRequest.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile attachmentPart = new MockMultipartFile(
                "attachments",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "hello".getBytes()
        );

        when(messageService.create(eq(request), any(List.class))).thenReturn(response);

        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart)
                        .file(attachmentPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.content").value("hello"))
                .andExpect(jsonPath("$.channelId").value(channelId.toString()))
                .andExpect(jsonPath("$.author.id").value(authorId.toString()))
                .andExpect(jsonPath("$.attachments[0].id").value(attachmentId.toString()))
                .andExpect(jsonPath("$.attachments[0].fileName").value("test.txt"));

        verify(messageService).create(eq(request), any(List.class));
    }

    @Test
    @DisplayName("메시지 생성 실패 - 요청 값 검증 실패")
    void create_fail_invalidRequest() throws Exception {
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest(
                "",
                channelId,
                authorId
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "messageCreateRequest.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("채널 ID로 메시지 목록 조회 성공")
    void findAllByChannelId_success() throws Exception {
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        UserResponse author = new UserResponse(
                authorId,
                "user1",
                "user1@test.com",
                null,
                true
        );

        MessageResponse messageResponse = new MessageResponse(
                messageId,
                Instant.now(),
                Instant.now(),
                "hello",
                channelId,
                author,
                List.of()
        );

        PageResponse<MessageResponse> pageResponse = new PageResponse<>(
                List.of(messageResponse),
                null,
                1,
                false,
                1L
        );

        when(messageService.findAllByChannelId(eq(channelId), eq(null), any(Pageable.class)))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/messages")
                        .param("channelId", channelId.toString())
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(messageId.toString()))
                .andExpect(jsonPath("$.content[0].content").value("hello"))
                .andExpect(jsonPath("$.content[0].channelId").value(channelId.toString()))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(messageService).findAllByChannelId(eq(channelId), eq(null), any(Pageable.class));
    }

    @Test
    @DisplayName("채널 ID로 메시지 목록 조회 실패 - channelId 파라미터 누락")
    void findAllByChannelId_fail_missingChannelId() throws Exception {
        mockMvc.perform(get("/api/messages"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void update_success() throws Exception {
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageUpdateRequest request = new MessageUpdateRequest("updated");

        UserResponse author = new UserResponse(
                authorId,
                "user1",
                "user1@test.com",
                null,
                true
        );

        MessageResponse response = new MessageResponse(
                messageId,
                Instant.now(),
                Instant.now(),
                "updated",
                channelId,
                author,
                List.of()
        );

        when(messageService.update(eq(messageId), eq(request))).thenReturn(response);

        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.content").value("updated"))
                .andExpect(jsonPath("$.channelId").value(channelId.toString()));

        verify(messageService).update(eq(messageId), eq(request));
    }

    @Test
    @DisplayName("메시지 수정 실패 - 요청 값 검증 실패")
    void update_fail_invalidRequest() throws Exception {
        UUID messageId = UUID.randomUUID();

        MessageUpdateRequest request = new MessageUpdateRequest("");

        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("메시지 수정 실패 - 메시지 없음")
    void update_fail_messageNotFound() throws Exception {
        UUID messageId = UUID.randomUUID();

        MessageUpdateRequest request = new MessageUpdateRequest("updated");

        when(messageService.update(eq(messageId), eq(request)))
                .thenThrow(new MessageNotFoundException(messageId));

        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.exceptionType").value("MessageNotFoundException"));

        verify(messageService).update(eq(messageId), eq(request));
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void delete_success() throws Exception {
        UUID messageId = UUID.randomUUID();

        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
                .andExpect(status().isNoContent());

        verify(messageService).delete(messageId);
    }

    @Test
    @DisplayName("메시지 삭제 실패 - 메시지 없음")
    void delete_fail_messageNotFound() throws Exception {
        UUID messageId = UUID.randomUUID();

        doThrow(new MessageNotFoundException(messageId))
                .when(messageService)
                .delete(messageId);

        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.exceptionType").value("MessageNotFoundException"));

        verify(messageService).delete(messageId);
    }
}