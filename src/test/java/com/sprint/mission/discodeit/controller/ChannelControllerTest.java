package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = ChannelController.class,
    excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JpaAuditingConfig.class))
@Import(GlobalExceptionHandler.class)
// 시큐리티 필터가 함께 적용되므로 인증된 사용자로 요청하고, 변경 요청에는 CSRF 토큰을 포함한다
@WithMockUser
class ChannelControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @MockitoBean ChannelService channelService;

  @Test
  @DisplayName("POST /api/channels/public - PUBLIC 채널 생성 성공")
  void createPublic_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    ChannelDto dto = new ChannelDto(channelId, ChannelType.PUBLIC, "general", "desc",
        List.of(), Instant.now());
    given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(dto);

    PublicChannelCreateRequest req = new PublicChannelCreateRequest("general", "desc");
    mockMvc.perform(post("/api/channels/public")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("general"))
        .andExpect(jsonPath("$.type").value("PUBLIC"));
  }

  @Test
  @DisplayName("DELETE /api/channels/{channelId} - 존재하지 않는 채널 삭제 시 404")
  void delete_notFound_returns404() throws Exception {
    UUID channelId = UUID.randomUUID();
    org.mockito.BDDMockito.willThrow(new ChannelNotFoundException(channelId))
        .given(channelService).delete(channelId);

    mockMvc.perform(delete("/api/channels/{channelId}", channelId).with(csrf()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
  }

  @Test
  @DisplayName("GET /api/channels?userId - 채널 목록 조회 성공")
  void findAll_success() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    ChannelDto dto = new ChannelDto(channelId, ChannelType.PUBLIC, "general", null, List.of(), null);
    given(channelService.findAllByUserId(userId)).willReturn(List.of(dto));

    mockMvc.perform(get("/api/channels").param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("general"));
  }
}
