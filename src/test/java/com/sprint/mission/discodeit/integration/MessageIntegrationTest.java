package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MessageIntegrationTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  private String userId;
  private String channelId;

  @BeforeEach
  void setUp() throws Exception {
    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new UserCreateRequest("msguser", "msguser@email.com", "password123!")));
    MvcResult userResult = mockMvc.perform(multipart("/api/users").file(userPart))
        .andExpect(status().isCreated())
        .andReturn();
    userId = objectMapper.readTree(userResult.getResponse().getContentAsString()).get("id").asText();

    MvcResult channelResult = mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new PublicChannelCreateRequest("test-channel", null))))
        .andExpect(status().isCreated())
        .andReturn();
    channelId = objectMapper.readTree(channelResult.getResponse().getContentAsString()).get("id").asText();
  }

  private String createMessage(String content) throws Exception {
    MockMultipartFile part = new MockMultipartFile(
        "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new MessageCreateRequest(content,
            UUID.fromString(channelId), UUID.fromString(userId))));
    MvcResult result = mockMvc.perform(multipart("/api/messages").file(part))
        .andExpect(status().isCreated())
        .andReturn();
    return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
  }

  @Test
  @DisplayName("POST /api/messages - 메시지 생성 성공")
  void createMessage_success() throws Exception {
    MockMultipartFile part = new MockMultipartFile(
        "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new MessageCreateRequest("Hello World",
            UUID.fromString(channelId), UUID.fromString(userId))));

    mockMvc.perform(multipart("/api/messages").file(part))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("Hello World"))
        .andExpect(jsonPath("$.channelId").value(channelId));
  }

  @Test
  @DisplayName("PATCH /api/messages/{messageId} - 메시지 수정 성공")
  void updateMessage_success() throws Exception {
    String messageId = createMessage("Original");

    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new MessageUpdateRequest("Updated"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("Updated"));
  }

  @Test
  @DisplayName("DELETE /api/messages/{messageId} - 삭제 후 재삭제 시 404 반환")
  void deleteMessage_thenNotFound() throws Exception {
    String messageId = createMessage("ToDelete");

    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent());

    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"));
  }

  @Test
  @DisplayName("GET /api/messages?channelId - 커서 없이 첫 페이지 조회")
  void findAllByChannelId_firstPage() throws Exception {
    createMessage("msg1");
    createMessage("msg2");

    mockMvc.perform(get("/api/messages").param("channelId", channelId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.hasNext").value(false));
  }
}
