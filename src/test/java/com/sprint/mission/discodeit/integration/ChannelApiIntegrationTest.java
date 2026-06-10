package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.ChannelPrivateCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelPublicCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
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
  @DisplayName("공개 채널을 생성할 수 있다")
  void createPublicChannel_success() throws Exception {
    // given
    ChannelPublicCreateRequest request = new ChannelPublicCreateRequest(
        "public-channel",
        "public description"
    );

    // when & then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.type").value("PUBLIC"))
        .andExpect(jsonPath("$.name").value("public-channel"))
        .andExpect(jsonPath("$.description").value("public description"))
        .andExpect(jsonPath("$.participants").isArray());
  }

  @Test
  @DisplayName("비공개 채널을 생성할 수 있다")
  void createPrivateChannel_success() throws Exception {
    // given
    UUID userId1 = createUser("user1", "user1@test.com");
    UUID userId2 = createUser("user2", "user2@test.com");

    ChannelPrivateCreateRequest request = new ChannelPrivateCreateRequest(
        "private-channel",
        List.of(userId1, userId2)
    );

    // when & then
    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.type").value("PRIVATE"))
        .andExpect(jsonPath("$.name").value("private-channel"))
        .andExpect(jsonPath("$.participants").isArray())
        .andExpect(jsonPath("$.participants.length()").value(2));
  }

  @Test
  @DisplayName("채널 정보를 수정할 수 있다")
  void updateChannel_success() throws Exception {
    // given
    UUID channelId = createPublicChannel("public-channel", "public description");

    ChannelUpdateRequest request = new ChannelUpdateRequest(
        "updated-channel",
        ""
    );

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.type").value("PUBLIC"))
        .andExpect(jsonPath("$.name").value("updated-channel"))
        .andExpect(jsonPath("$.description").value("public description"))
        .andExpect(jsonPath("$.participants").isArray());
  }

  @Test
  @DisplayName("채널을 삭제할 수 있다")
  void deleteChannel_success() throws Exception {
    // given
    UUID channelId = createPublicChannel("public-channel", "public description");

    // when & then
    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("사용자 ID로 채널 목록을 조회할 수 있다")
  void findAllByUserId_success() throws Exception {
    // given
    UUID userId1 = createUser("user1", "user1@test.com");
    UUID userId2 = createUser("user2", "user2@test.com");

    createPublicChannel("public-channel", "public description");

    createPrivateChannel(
        "private-channel",
        List.of(userId1, userId2)
    );

    // when & then
    mockMvc.perform(get("/api/channels")
            .param("userId", userId1.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  @DisplayName("존재하지 않는 채널 수정 요청이면 404를 반환한다")
  void updateChannel_fail_channelNotFound() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();

    ChannelUpdateRequest request = new ChannelUpdateRequest(
        "updated-channel",
        "updated description"
    );

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("존재하지 않는 채널 삭제 요청이면 404를 반환한다")
  void deleteChannel_fail_channelNotFound() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();

    // when & then
    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNotFound());
  }

  private UUID createPublicChannel(String name, String description) throws Exception {
    ChannelPublicCreateRequest request = new ChannelPublicCreateRequest(
        name,
        description
    );

    MvcResult result = mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isCreated())
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    JsonNode root = objectMapper.readTree(responseBody);

    return UUID.fromString(root.get("id").asText());
  }

  private UUID createPrivateChannel(String name, List<UUID> participantIds) throws Exception {
    ChannelPrivateCreateRequest request = new ChannelPrivateCreateRequest(
        name,
        participantIds
    );

    MvcResult result = mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isCreated())
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    JsonNode root = objectMapper.readTree(responseBody);

    return UUID.fromString(root.get("id").asText());
  }

  private UUID createUser(String username, String email) throws Exception {
    UserCreateRequest request = new UserCreateRequest(
        username,
        email,
        "password"
    );

    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    MvcResult result = mockMvc.perform(multipart("/api/users")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    JsonNode root = objectMapper.readTree(responseBody);

    return UUID.fromString(root.get("id").asText());
  }

}
