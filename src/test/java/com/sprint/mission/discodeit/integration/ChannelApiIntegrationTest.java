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
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
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
class ChannelApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private BinaryContentStorage binaryContentStorage;

  // ── create public ────────────────────────────────────────────────────────

  @Test
  @DisplayName("공개 채널 생성 성공 - 201")
  void createPublicChannel() throws Exception {
    mockMvc.perform(post("/api/channels/public").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelCreateRequest("general", "일반 채널"))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("general"))
        .andExpect(jsonPath("$.description").value("일반 채널"))
        .andExpect(jsonPath("$.type").value("PUBLIC"));
  }

  @Test
  @DisplayName("공개 채널 생성 실패 - 400 (이름 누락)")
  void createPublicChannel_blankName() throws Exception {
    mockMvc.perform(post("/api/channels/public").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelCreateRequest("", "설명"))))
        .andExpect(status().isBadRequest());
  }

  // ── create private ───────────────────────────────────────────────────────

  @Test
  @DisplayName("비공개 채널 생성 성공 - 201")
  void createPrivateChannel() throws Exception {
    String userId1 = createUser("privUser1", "priv1@test.com");
    String userId2 = createUser("privUser2", "priv2@test.com");

    mockMvc.perform(post("/api/channels/private").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PrivateChannelCreateRequest(
                    List.of(UUID.fromString(userId1), UUID.fromString(userId2))))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value("PRIVATE"));
  }

  @Test
  @DisplayName("비공개 채널 생성 실패 - 400 (참여자 목록 없음)")
  void createPrivateChannel_emptyParticipants() throws Exception {
    mockMvc.perform(post("/api/channels/private").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PrivateChannelCreateRequest(List.of()))))
        .andExpect(status().isBadRequest());
  }

  // ── update ───────────────────────────────────────────────────────────────

  @Test
  @DisplayName("공개 채널 수정 성공 - 200")
  void update() throws Exception {
    String channelId = createPublicChannel("old-name", "old desc");

    mockMvc.perform(patch("/api/channels/{channelId}", channelId).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelUpdateRequest("new-name", "new desc"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("new-name"))
        .andExpect(jsonPath("$.description").value("new desc"));
  }

  @Test
  @DisplayName("공개 채널 수정 실패 - 404 (존재하지 않는 채널)")
  void update_notFound() throws Exception {
    mockMvc.perform(patch("/api/channels/{channelId}", UUID.randomUUID()).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelUpdateRequest("new-name", null))))
        .andExpect(status().isNotFound());
  }

  // ── delete ───────────────────────────────────────────────────────────────

  @Test
  @DisplayName("채널 삭제 성공 - 204")
  void deleteChannel() throws Exception {
    String channelId = createPublicChannel("to-delete", null);

    mockMvc.perform(delete("/api/channels/{channelId}", channelId).with(csrf()))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("채널 삭제 실패 - 404 (존재하지 않는 채널)")
  void delete_notFound() throws Exception {
    mockMvc.perform(delete("/api/channels/{channelId}", UUID.randomUUID()).with(csrf()))
        .andExpect(status().isNotFound());
  }

  // ── findAllByUserId ───────────────────────────────────────────────────────

  @Test
  @DisplayName("사용자의 채널 목록 조회 성공 - 200")
  void findAllByUserId() throws Exception {
    createPublicChannel("ch1", null);
    createPublicChannel("ch2", null);
    String userId = createUser("listChanUser", "listchan@test.com");

    mockMvc.perform(get("/api/channels")
            .param("userId", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  // ── helper ───────────────────────────────────────────────────────────────

  private String createPublicChannel(String name, String description) throws Exception {
    MvcResult result = mockMvc.perform(post("/api/channels/public").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelCreateRequest(name, description))))
        .andExpect(status().isCreated())
        .andReturn();

    return objectMapper.readTree(result.getResponse().getContentAsString())
        .get("id").asText();
  }

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
}
