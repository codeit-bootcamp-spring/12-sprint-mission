package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
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
class ChannelApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("공개 채널 생성 성공")
  void createPublic_success() throws Exception {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest(
        "공지사항",
        "공지 채널입니다."
    );

    // when & then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value("PUBLIC"))
        .andExpect(jsonPath("$.name").value("공지사항"))
        .andExpect(jsonPath("$.description").value("공지 채널입니다."));
  }

  @Test
  @DisplayName("공개 채널 생성 실패 - 검증 실패")
  void createPublic_fail_validation() throws Exception {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest(
        "",
        "공지 채널입니다."
    );

    // when & then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("공개 채널 수정 성공")
  void update_success() throws Exception {
    // given
    UUID channelId = createPublicChannel("기존 채널", "기존 설명");

    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
        "수정된 채널",
        "수정된 설명"
    );

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("수정된 채널"))
        .andExpect(jsonPath("$.description").value("수정된 설명"));
  }

  @Test
  @DisplayName("채널 삭제 성공")
  void delete_success() throws Exception {
    // given
    UUID channelId = createPublicChannel("삭제 채널", "삭제 설명");

    // when & then
    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());
  }

  private UUID createPublicChannel(String name, String description) throws Exception {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest(
        name,
        description
    );

    String responseBody = mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode jsonNode = objectMapper.readTree(responseBody);
    return UUID.fromString(jsonNode.get("id").asText());
  }
}