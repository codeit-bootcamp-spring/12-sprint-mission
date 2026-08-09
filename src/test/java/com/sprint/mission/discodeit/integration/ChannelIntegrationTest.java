package com.sprint.mission.discodeit.integration;

import static com.sprint.mission.discodeit.support.SecurityTestSupport.asUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Role;
import java.util.List;
import java.util.UUID;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ChannelIntegrationTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  // 퍼블릭 채널 생성/수정/삭제는 CHANNEL_MANAGER 권한이 필요하다
  private final RequestPostProcessor channelManager =
      asUser(UUID.randomUUID(), "manager", Role.CHANNEL_MANAGER);

  private String createUser(String username, String email) throws Exception {
    MockMultipartFile part = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new UserCreateRequest(username, email, "password123!")));
    MvcResult result = mockMvc.perform(multipart("/api/users").file(part).with(csrf()))
        .andExpect(status().isCreated())
        .andReturn();
    return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
  }

  private String createPublicChannel(String name) throws Exception {
    MvcResult result = mockMvc.perform(post("/api/channels/public")
            .with(channelManager)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new PublicChannelCreateRequest(name, null))))
        .andExpect(status().isCreated())
        .andReturn();
    return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
  }

  @Test
  @DisplayName("POST /api/channels/public - CHANNEL_MANAGER는 PUBLIC 채널 생성 성공")
  void createPublicChannel_success() throws Exception {
    mockMvc.perform(post("/api/channels/public")
            .with(channelManager)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelCreateRequest("general", "General channel"))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value("PUBLIC"))
        .andExpect(jsonPath("$.name").value("general"));
  }

  @Test
  @DisplayName("POST /api/channels/public - USER 권한으로는 403 반환")
  void createPublicChannel_withoutRole_returns403() throws Exception {
    mockMvc.perform(post("/api/channels/public")
            .with(asUser(UUID.randomUUID(), "normal", Role.USER))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelCreateRequest("general", "General channel"))))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
  }

  @Test
  @DisplayName("PATCH /api/channels/{channelId} - PRIVATE 채널 수정 시 400 반환")
  void updatePrivateChannel_returns400() throws Exception {
    String userId = createUser("chuser", "chuser@email.com");

    MvcResult result = mockMvc.perform(post("/api/channels/private")
            .with(asUser(userId, "chuser", Role.USER))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PrivateChannelCreateRequest(List.of(UUID.fromString(userId))))))
        .andExpect(status().isCreated())
        .andReturn();
    String channelId = objectMapper.readTree(result.getResponse().getContentAsString())
        .get("id").asText();

    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .with(channelManager)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelUpdateRequest("new name", "desc"))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("PRIVATE_CHANNEL_UPDATE"));
  }

  @Test
  @DisplayName("DELETE /api/channels/{channelId} - 삭제 후 재삭제 시 404 반환")
  void deleteChannel_thenNotFound() throws Exception {
    String channelId = createPublicChannel("temp");

    mockMvc.perform(delete("/api/channels/{channelId}", channelId)
            .with(channelManager)
            .with(csrf()))
        .andExpect(status().isNoContent());

    mockMvc.perform(delete("/api/channels/{channelId}", channelId)
            .with(channelManager)
            .with(csrf()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
  }

  @Test
  @DisplayName("GET /api/channels?userId - PUBLIC 채널 포함 목록 조회 성공")
  void findAllByUserId_includesPublicChannels() throws Exception {
    String userId = createUser("listuser", "listuser@email.com");
    createPublicChannel("pub1");
    createPublicChannel("pub2");

    mockMvc.perform(get("/api/channels")
            .param("userId", userId)
            .with(asUser(userId, "listuser", Role.USER)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.type == 'PUBLIC')]").exists());
  }
}
