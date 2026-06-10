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
import com.sprint.mission.discodeit.dto.channel.ChannelPublicCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import java.nio.charset.StandardCharsets;
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
class MessageApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("메시지를 생성할 수 있다")
  void createMessage_success() throws Exception {
    // given
    UUID authorId = createUser("user1", "user1@test.com");
    UUID channelId = createPublicChannel("public-channel", "public description");

    MessageCreateRequest request = new MessageCreateRequest(
        "hello message",
        channelId,
        authorId
    );

    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.updatedAt").exists())
        .andExpect(jsonPath("$.content").value("hello message"))
        .andExpect(jsonPath("$.channelId").value(channelId.toString()))
        .andExpect(jsonPath("$.author.id").value(authorId.toString()))
        .andExpect(jsonPath("$.author.username").value("user1"))
        .andExpect(jsonPath("$.author.email").value("user1@test.com"))
        .andExpect(jsonPath("$.attachments").isArray());
  }

  @Test
  @DisplayName("첨부파일과 함께 메시지를 생성할 수 있다")
  void createMessage_withAttachments_success() throws Exception {
    // given
    UUID authorId = createUser("user1", "user1@test.com");
    UUID channelId = createPublicChannel("public-channel", "public description");

    MessageCreateRequest request = new MessageCreateRequest(
        "message with attachment",
        channelId,
        authorId
    );

    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    MockMultipartFile attachment = new MockMultipartFile(
        "attachments",
        "test.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "file-content".getBytes(StandardCharsets.UTF_8)
    );

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(requestPart)
            .file(attachment)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.content").value("message with attachment"))
        .andExpect(jsonPath("$.channelId").value(channelId.toString()))
        .andExpect(jsonPath("$.author.id").value(authorId.toString()))
        .andExpect(jsonPath("$.attachments").isArray());
  }

  @Test
  @DisplayName("메시지를 수정할 수 있다")
  void updateMessage_success() throws Exception {
    // given
    UUID authorId = createUser("user1", "user1@test.com");
    UUID channelId = createPublicChannel("public-channel", "public description");
    UUID messageId = createMessage(authorId, channelId, "hello message");

    MessageUpdateRequest request = new MessageUpdateRequest(
        "updated message"
    );

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.content").value("updated message"))
        .andExpect(jsonPath("$.channelId").value(channelId.toString()))
        .andExpect(jsonPath("$.author.id").value(authorId.toString()))
        .andExpect(jsonPath("$.attachments").isArray());
  }

  @Test
  @DisplayName("메시지를 삭제할 수 있다")
  void deleteMessage_success() throws Exception {
    // given
    UUID authorId = createUser("user1", "user1@test.com");
    UUID channelId = createPublicChannel("public-channel", "public description");
    UUID messageId = createMessage(authorId, channelId, "hello message");

    // when & then
    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("채널 ID로 메시지 목록을 페이지 조회할 수 있다")
  void findMessagesByChannelId_success() throws Exception {
    // given
    UUID authorId = createUser("user1", "user1@test.com");
    UUID channelId = createPublicChannel("public-channel", "public description");

    createMessage(authorId, channelId, "hello message 1");
    createMessage(authorId, channelId, "hello message 2");

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString())
            .param("page", "0"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.number").value(0))
        .andExpect(jsonPath("$.hasNext").exists());
  }

  @Test
  @DisplayName("존재하지 않는 메시지 수정 요청이면 404를 반환한다")
  void updateMessage_fail_messageNotFound() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();

    MessageUpdateRequest request = new MessageUpdateRequest(
        "updated message"
    );

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("존재하지 않는 메시지 삭제 요청이면 404를 반환한다")
  void deleteMessage_fail_messageNotFound() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();

    // when & then
    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNotFound());
  }

  private UUID createMessage(UUID authorId, UUID channelId, String content) throws Exception {
    MessageCreateRequest request = new MessageCreateRequest(
        content,
        channelId,
        authorId
    );

    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    MvcResult result = mockMvc.perform(multipart("/api/messages")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    JsonNode root = objectMapper.readTree(responseBody);

    return UUID.fromString(root.get("id").asText());
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