package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
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

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  @DisplayName("메시지 생성 성공")
  void create_success() throws Exception {
    // given
    User author = saveUser("minji", "minji@test.com");
    Channel channel = saveChannel("공지사항", "공지 채널입니다.");

    MessageCreateRequest request = new MessageCreateRequest(
        "안녕하세요",
        channel.getId(),
        author.getId()
    );

    MockMultipartFile messageCreateRequest = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(messageCreateRequest))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("안녕하세요"))
        .andExpect(jsonPath("$.channelId").value(channel.getId().toString()))
        .andExpect(jsonPath("$.author.username").value("minji"));
  }

  @Test
  @DisplayName("메시지 생성 실패 - 검증 실패")
  void create_fail_validation() throws Exception {
    // given
    User author = saveUser("failUser", "fail@test.com");
    Channel channel = saveChannel("실패 채널", "실패 채널입니다.");

    MessageCreateRequest request = new MessageCreateRequest(
        "",
        channel.getId(),
        author.getId()
    );

    MockMultipartFile messageCreateRequest = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(messageCreateRequest))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("메시지 수정 성공")
  void update_success() throws Exception {
    // given
    UUID messageId = createMessage("기존 메시지");

    MessageUpdateRequest request = new MessageUpdateRequest("수정된 메시지");

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("수정된 메시지"));
  }

  @Test
  @DisplayName("메시지 삭제 성공")
  void delete_success() throws Exception {
    // given
    UUID messageId = createMessage("삭제할 메시지");

    // when & then
    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("채널 메시지 목록 조회 성공")
  void findAllByChannelId_success() throws Exception {
    // given
    User author = saveUser("listUser", "list@test.com");
    Channel channel = saveChannel("목록 채널", "목록 채널입니다.");

    createMessage("목록 메시지", channel.getId(), author.getId());

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channel.getId().toString())
            .param("size", "10")
            .param("sort", "createdAt,desc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].content").value("목록 메시지"));
  }

  private UUID createMessage(String content) throws Exception {
    User author = saveUser("user" + UUID.randomUUID(), UUID.randomUUID() + "@test.com");
    Channel channel = saveChannel("채널" + UUID.randomUUID(), "설명");

    return createMessage(content, channel.getId(), author.getId());
  }

  private UUID createMessage(String content, UUID channelId, UUID authorId) throws Exception {
    MessageCreateRequest request = new MessageCreateRequest(
        content,
        channelId,
        authorId
    );

    MockMultipartFile messageCreateRequest = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    String responseBody = mockMvc.perform(multipart("/api/messages")
            .file(messageCreateRequest))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode jsonNode = objectMapper.readTree(responseBody);
    return UUID.fromString(jsonNode.get("id").asText());
  }

  private User saveUser(String username, String email) {
    User user = new User(username, email, "1234", null);
    new UserStatus(user, Instant.now());

    return userRepository.save(user);
  }

  private Channel saveChannel(String name, String description) {
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);
    return channelRepository.save(channel);
  }
}