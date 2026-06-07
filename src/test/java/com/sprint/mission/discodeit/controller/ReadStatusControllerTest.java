package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.readstatus.DuplicateReadStatusException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.service.ReadStatusService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReadStatusController.class)
@Import(GlobalExceptionHandler.class)
class ReadStatusControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ReadStatusService readStatusService;

  private UUID readStatusId;
  private UUID userId;
  private UUID channelId;
  private ReadStatusDto readStatusDto;

  @BeforeEach
  void setUp() {
    readStatusId = UUID.randomUUID();
    userId = UUID.randomUUID();
    channelId = UUID.randomUUID();
    readStatusDto = new ReadStatusDto(readStatusId, userId, channelId, Instant.now());
  }

  // ── create ───────────────────────────────────────────────

  @Test
  @DisplayName("읽음 상태 생성 성공")
  void createReadStatus_Success() throws Exception {
    // given
    given(readStatusService.create(any(ReadStatusCreateRequest.class))).willReturn(readStatusDto);

    // when & then
    mockMvc.perform(post("/api/readStatuses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new ReadStatusCreateRequest(userId, channelId, Instant.now()))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").value(userId.toString()))
        .andExpect(jsonPath("$.channelId").value(channelId.toString()));
  }

  @Test
  @DisplayName("이미 존재하는 읽음 상태 생성 시 409 반환")
  void createReadStatus_Duplicate_Returns409() throws Exception {
    // given
    given(readStatusService.create(any(ReadStatusCreateRequest.class)))
        .willThrow(new DuplicateReadStatusException());

    // when & then
    mockMvc.perform(post("/api/readStatuses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new ReadStatusCreateRequest(userId, channelId, Instant.now()))))
        .andExpect(status().isConflict());
  }

  // ── update ───────────────────────────────────────────────

  @Test
  @DisplayName("읽음 상태 수정 성공")
  void updateReadStatus_Success() throws Exception {
    // given
    given(readStatusService.update(eq(readStatusId), any(ReadStatusUpdateRequest.class)))
        .willReturn(readStatusDto);

    // when & then
    mockMvc.perform(patch("/api/readStatuses/{readStatusId}", readStatusId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new ReadStatusUpdateRequest(Instant.now()))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(readStatusId.toString()));
  }

  @Test
  @DisplayName("존재하지 않는 읽음 상태 수정 시 404 반환")
  void updateReadStatus_NotFound_Returns404() throws Exception {
    // given
    given(readStatusService.update(eq(readStatusId), any(ReadStatusUpdateRequest.class)))
        .willThrow(new ReadStatusNotFoundException());

    // when & then
    mockMvc.perform(patch("/api/readStatuses/{readStatusId}", readStatusId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new ReadStatusUpdateRequest(Instant.now()))))
        .andExpect(status().isNotFound());
  }

  // ── findAllByUserId ──────────────────────────────────────

  @Test
  @DisplayName("userId로 읽음 상태 목록 조회 성공")
  void findAllByUserId_Success() throws Exception {
    // given
    given(readStatusService.findAllByUserId(eq(userId))).willReturn(List.of(readStatusDto));

    // when & then
    mockMvc.perform(get("/api/readStatuses")
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].userId").value(userId.toString()));
  }

  @Test
  @DisplayName("읽음 상태 없으면 빈 목록 반환")
  void findAllByUserId_Empty_ReturnsEmptyList() throws Exception {
    // given
    given(readStatusService.findAllByUserId(eq(userId))).willReturn(List.of());

    // when & then
    mockMvc.perform(get("/api/readStatuses")
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }
}