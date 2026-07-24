package com.sprint.mission.discodeit.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
@Transactional
class MessageApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private BinaryContentStorage binaryContentStorage;

  // ── create ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("메시지 생성 성공 - 201")
  void create() throws Exception {
    String userId = createUser("msgUser", "msg@test.com");
    String channelId = createChannel("msg-channel");

    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new MessageCreateRequest("안녕하세요!", UUID.fromString(channelId),
                UUID.fromString(userId))));

    mockMvc.perform(multipart("/api/messages")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(csrf()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("안녕하세요!"))
        .andExpect(jsonPath("$.channelId").value(channelId));
  }

  @Test
  @DisplayName("메시지 생성 실패 - 400 (내용 없음)")
  void create_blankContent() throws Exception {
    String userId = createUser("blankUser", "blank@test.com");
    String channelId = createChannel("blank-channel");

    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new MessageCreateRequest("", UUID.fromString(channelId), UUID.fromString(userId))));

    mockMvc.perform(multipart("/api/messages")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  // ── update ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("메시지 수정 성공 - 200")
  void update() throws Exception {
    String userId = createUser("updateMsgUser", "updatemsg@test.com");
    String channelId = createChannel("update-msg-channel");
    String messageId = createMessage("원본 내용", channelId, userId);

    mockMvc.perform(patch("/api/messages/{messageId}", messageId).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new MessageUpdateRequest("수정된 내용"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("수정된 내용"));
  }

  @Test
  @DisplayName("메시지 수정 실패 - 404 (존재하지 않는 메시지)")
  void update_notFound() throws Exception {
    mockMvc.perform(patch("/api/messages/{messageId}", UUID.randomUUID()).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new MessageUpdateRequest("수정 내용"))))
        .andExpect(status().isNotFound());
  }

  // ── delete ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("메시지 삭제 성공 - 204")
  void deleteMessage() throws Exception {
    String userId = createUser("deleteMsgUser", "deletemsg@test.com");
    String channelId = createChannel("delete-msg-channel");
    String messageId = createMessage("삭제할 메시지", channelId, userId);

    mockMvc.perform(delete("/api/messages/{messageId}", messageId).with(csrf()))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("메시지 삭제 실패 - 404 (존재하지 않는 메시지)")
  void delete_notFound() throws Exception {
    mockMvc.perform(delete("/api/messages/{messageId}", UUID.randomUUID()).with(csrf()))
        .andExpect(status().isNotFound());
  }

  // ── findByChannelId ──────────────────────────────────────────────────────

  @Test
  @DisplayName("채널 메시지 목록 조회 성공 - 200")
  void findByChannelId() throws Exception {
    String userId = createUser("listMsgUser", "listmsg@test.com");
    String channelId = createChannel("list-msg-channel");
    createMessage("첫 번째 메시지", channelId, userId);
    createMessage("두 번째 메시지", channelId, userId);

    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2));
  }

  @Test
  @DisplayName("채널 메시지 목록 조회 성공 - 빈 채널")
  void findByChannelId_emptyChannel() throws Exception {
    String channelId = createChannel("empty-channel");

    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(0));
  }

  // ── helper ──────────────────────────────────────────────────────────────

  private String createUser(String username, String email) throws Exception {
    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new UserCreateRequest(username, email, "password1234!")));

    MvcResult result = mockMvc.perform(multipart("/api/users")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(csrf()))
        .andExpect(status().isCreated())
        .andReturn();

    return objectMapper.readTree(result.getResponse().getContentAsString())
        .get("id").asText();
  }

  private String createChannel(String name) throws Exception {
    MvcResult result = mockMvc.perform(post("/api/channels/public").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelCreateRequest(name, null))))
        .andExpect(status().isCreated())
        .andReturn();

    return objectMapper.readTree(result.getResponse().getContentAsString())
        .get("id").asText();
  }

  private String createMessage(String content, String channelId, String authorId) throws Exception {
    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new MessageCreateRequest(content, UUID.fromString(channelId),
                UUID.fromString(authorId))));

    MvcResult result = mockMvc.perform(multipart("/api/messages")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(csrf()))
        .andExpect(status().isCreated())
        .andReturn();

    return objectMapper.readTree(result.getResponse().getContentAsString())
        .get("id").asText();
  }
}
