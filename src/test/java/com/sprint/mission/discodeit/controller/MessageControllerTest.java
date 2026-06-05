package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import com.sprint.mission.discodeit.dto.data.MessageDto;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MessageService messageService;

  @MockitoBean
  private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  // ── create ───────────────────────────────────────────────────────────────

  @Test
  @DisplayName("메시지 생성 성공 - 201")
  void create_success() throws Exception {
    UUID messageId = UuidCreator.getTimeOrderedEpoch();
    UUID channelId = UuidCreator.getTimeOrderedEpoch();
    UUID authorId = UuidCreator.getTimeOrderedEpoch();
    MessageDto messageDto = new MessageDto(messageId, Instant.now(), Instant.now(), "안녕하세요",
        channelId, null, List.of());
    given(messageService.create(any(MessageCreateRequest.class), any())).willReturn(messageDto);

    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new MessageCreateRequest("안녕하세요", channelId, authorId)));

    mockMvc.perform(multipart("/api/messages").file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.content").value("안녕하세요"))
        .andExpect(jsonPath("$.channelId").value(channelId.toString()));
  }

  @Test
  @DisplayName("메시지 생성 실패 - 400 (내용 없음)")
  void create_fail_blankContent() throws Exception {
    UUID channelId = UuidCreator.getTimeOrderedEpoch();
    UUID authorId = UuidCreator.getTimeOrderedEpoch();
    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new MessageCreateRequest("", channelId, authorId)));

    mockMvc.perform(multipart("/api/messages").file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  // ── update ───────────────────────────────────────────────────────────────

  @Test
  @DisplayName("메시지 수정 성공 - 200")
  void update_success() throws Exception {
    UUID messageId = UuidCreator.getTimeOrderedEpoch();
    UUID channelId = UuidCreator.getTimeOrderedEpoch();
    MessageDto updatedMessage = new MessageDto(messageId, Instant.now(), Instant.now(), "수정된 내용",
        channelId, null, List.of());
    given(messageService.update(eq(messageId), any(MessageUpdateRequest.class))).willReturn(
        updatedMessage);

    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new MessageUpdateRequest("수정된 내용"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.content").value("수정된 내용"));
  }

  @Test
  @DisplayName("메시지 수정 실패 - 400 (빈 내용)")
  void update_fail_blankContent() throws Exception {
    UUID messageId = UuidCreator.getTimeOrderedEpoch();

    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new MessageUpdateRequest(""))))
        .andExpect(status().isBadRequest());
  }

  // ── delete ───────────────────────────────────────────────────────────────

  @Test
  @DisplayName("메시지 삭제 성공 - 204")
  void delete_success() throws Exception {
    UUID messageId = UuidCreator.getTimeOrderedEpoch();
    willDoNothing().given(messageService).delete(messageId);

    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("메시지 삭제 실패 - 404 (존재하지 않는 메시지)")
  void delete_fail_messageNotFound() throws Exception {
    UUID messageId = UuidCreator.getTimeOrderedEpoch();
    willThrow(MessageNotFoundException.withId(messageId)).given(messageService).delete(messageId);

    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"));
  }

  // ── findByChannelId ───────────────────────────────────────────────────────

  @Test
  @DisplayName("채널 메시지 목록 조회 성공 - 200")
  void findByChannelId_success() throws Exception {
    UUID channelId = UuidCreator.getTimeOrderedEpoch();
    UUID messageId = UuidCreator.getTimeOrderedEpoch();
    MessageDto messageDto = new MessageDto(messageId, Instant.now(), Instant.now(), "테스트 메시지",
        channelId, null, List.of());
    PageResponse<MessageDto> pageResponse = new PageResponse<>(List.of(messageDto), null, 1, false);
    given(messageService.findAllByChannelId(any(), any(), any())).willReturn(pageResponse);

    mockMvc.perform(get("/api/messages").param("channelId", channelId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(messageId.toString()))
        .andExpect(jsonPath("$.content[0].content").value("테스트 메시지"))
        .andExpect(jsonPath("$.hasNext").value(false));
  }

  @Test
  @DisplayName("채널 메시지 목록 조회 - 빈 목록 반환")
  void findByChannelId_emptyList() throws Exception {
    UUID channelId = UuidCreator.getTimeOrderedEpoch();
    PageResponse<MessageDto> pageResponse = new PageResponse<>(List.of(), null, 0, false);
    given(messageService.findAllByChannelId(any(), any(), any())).willReturn(pageResponse);

    mockMvc.perform(get("/api/messages").param("channelId", channelId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isEmpty())
        .andExpect(jsonPath("$.hasNext").value(false));
  }
}
