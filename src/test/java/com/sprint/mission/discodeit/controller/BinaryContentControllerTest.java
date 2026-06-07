package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BinaryContentController.class)
@Import(GlobalExceptionHandler.class)
class BinaryContentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private BinaryContentService binaryContentService;

  @MockitoBean
  private BinaryContentStorage binaryContentStorage;

  private UUID contentId;
  private BinaryContentDto binaryContentDto;

  @BeforeEach
  void setUp() {
    contentId = UUID.randomUUID();
    binaryContentDto = new BinaryContentDto(contentId, "image.png", 1024L, "image/png");
  }

  // ── find ─────────────────────────────────────────────────

  @Test
  @DisplayName("단건 바이너리 콘텐츠 조회 성공")
  void findBinaryContent_Success() throws Exception {
    // given
    given(binaryContentService.find(eq(contentId))).willReturn(binaryContentDto);

    // when & then
    mockMvc.perform(get("/api/binaryContents/{binaryContentId}", contentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(contentId.toString()))
        .andExpect(jsonPath("$.fileName").value("image.png"));
  }

  @Test
  @DisplayName("존재하지 않는 바이너리 콘텐츠 조회 시 404 반환")
  void findBinaryContent_NotFound_Returns404() throws Exception {
    // given
    given(binaryContentService.find(eq(contentId)))
        .willThrow(new BinaryContentNotFoundException());

    // when & then
    mockMvc.perform(get("/api/binaryContents/{binaryContentId}", contentId))
        .andExpect(status().isNotFound());
  }

  // ── findAllByIdIn ────────────────────────────────────────

  @Test
  @DisplayName("ID 목록으로 바이너리 콘텐츠 다건 조회 성공")
  void findAllByIdIn_Success() throws Exception {
    // given
    UUID otherId = UUID.randomUUID();
    given(binaryContentService.findAllByIdIn(List.of(contentId, otherId)))
        .willReturn(List.of(binaryContentDto));

    // when & then
    mockMvc.perform(get("/api/binaryContents")
            .param("binaryContentIds", contentId.toString(), otherId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(contentId.toString()));
  }

  @Test
  @DisplayName("ID 목록 조회 결과 없으면 빈 목록 반환")
  void findAllByIdIn_Empty_ReturnsEmptyList() throws Exception {
    // given
    given(binaryContentService.findAllByIdIn(List.of(contentId))).willReturn(List.of());

    // when & then
    mockMvc.perform(get("/api/binaryContents")
            .param("binaryContentIds", contentId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }

  // ── download ─────────────────────────────────────────────

  @Test
  @DisplayName("바이너리 콘텐츠 다운로드 성공")
  void downloadBinaryContent_Success() throws Exception {
    // given
    given(binaryContentService.find(eq(contentId))).willReturn(binaryContentDto);
    given(binaryContentStorage.download(eq(binaryContentDto)))
        .willReturn(ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"image.png\"")
            .contentType(MediaType.IMAGE_PNG)
            .build());

    // when & then
    mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", contentId))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("존재하지 않는 콘텐츠 다운로드 시 404 반환")
  void downloadBinaryContent_NotFound_Returns404() throws Exception {
    // given
    given(binaryContentService.find(eq(contentId)))
        .willThrow(new BinaryContentNotFoundException());

    // when & then
    mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", contentId))
        .andExpect(status().isNotFound());
  }
}