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
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
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

    @Test
    @DisplayName("메시지 생성 API 통합 테스트")
    void createMessage_success() throws Exception {
        UUID userId = createUser("user1", "user1@test.com", "password");
        UUID channelId = createPublicChannel("channel", "description");

        MessageCreateRequest request = new MessageCreateRequest(
                "hello",
                channelId,
                userId
        );

        MockMultipartFile messageCreateRequestPart = new MockMultipartFile(
                "messageCreateRequest",
                "messageCreateRequest.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile attachmentPart = new MockMultipartFile(
                "attachments",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "hello".getBytes()
        );

        mockMvc.perform(multipart("/api/messages")
                        .file(messageCreateRequestPart)
                        .file(attachmentPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.content").value("hello"))
                .andExpect(jsonPath("$.channelId").value(channelId.toString()))
                .andExpect(jsonPath("$.author.id").value(userId.toString()))
                .andExpect(jsonPath("$.attachments[0].fileName").value("test.txt"));
    }

    @Test
    @DisplayName("메시지 생성 API 실패 - 존재하지 않는 채널")
    void createMessage_fail_channelNotFound() throws Exception {
        UUID userId = createUser("user1", "user1@test.com", "password");
        UUID unknownChannelId = UUID.randomUUID();

        MessageCreateRequest request = new MessageCreateRequest(
                "hello",
                unknownChannelId,
                userId
        );

        MockMultipartFile messageCreateRequestPart = new MockMultipartFile(
                "messageCreateRequest",
                "messageCreateRequest.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/messages")
                        .file(messageCreateRequestPart))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("채널 ID로 메시지 목록 조회 API 통합 테스트")
    void findAllByChannelId_success() throws Exception {
        UUID userId = createUser("user1", "user1@test.com", "password");
        UUID channelId = createPublicChannel("channel", "description");

        UUID messageId = createMessage("hello", channelId, userId);

        mockMvc.perform(get("/api/messages")
                        .param("channelId", channelId.toString())
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.id == '%s')]".formatted(messageId)).exists())
                .andExpect(jsonPath("$.content[0].content").value("hello"));
    }

    @Test
    @DisplayName("메시지 수정 API 통합 테스트")
    void updateMessage_success() throws Exception {
        UUID userId = createUser("user1", "user1@test.com", "password");
        UUID channelId = createPublicChannel("channel", "description");
        UUID messageId = createMessage("hello", channelId, userId);

        MessageUpdateRequest request = new MessageUpdateRequest("updated");

        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.content").value("updated"))
                .andExpect(jsonPath("$.channelId").value(channelId.toString()));
    }

    @Test
    @DisplayName("메시지 수정 API 실패 - 존재하지 않는 메시지")
    void updateMessage_fail_messageNotFound() throws Exception {
        UUID unknownMessageId = UUID.randomUUID();

        MessageUpdateRequest request = new MessageUpdateRequest("updated");

        mockMvc.perform(patch("/api/messages/{messageId}", unknownMessageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("메시지 삭제 API 통합 테스트")
    void deleteMessage_success() throws Exception {
        UUID userId = createUser("user1", "user1@test.com", "password");
        UUID channelId = createPublicChannel("channel", "description");
        UUID messageId = createMessage("hello", channelId, userId);

        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
                .andExpect(status().isNoContent());

        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MessageUpdateRequest("updated"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404));
    }

    private UUID createUser(String username, String email, String password) throws Exception {
        UserCreateRequest request = new UserCreateRequest(
                username,
                email,
                password
        );

        MockMultipartFile userCreateRequestPart = new MockMultipartFile(
                "userCreateRequest",
                "userCreateRequest.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        String responseBody = mockMvc.perform(multipart("/api/users")
                        .file(userCreateRequestPart))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return UUID.fromString(jsonNode.get("id").asText());
    }

    private UUID createPublicChannel(String name, String description) throws Exception {
        CreatePublicChannelRequest request = new CreatePublicChannelRequest(
                name,
                description
        );

        String responseBody = mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return UUID.fromString(jsonNode.get("id").asText());
    }

    private UUID createMessage(String content, UUID channelId, UUID authorId) throws Exception {
        MessageCreateRequest request = new MessageCreateRequest(
                content,
                channelId,
                authorId
        );

        MockMultipartFile messageCreateRequestPart = new MockMultipartFile(
                "messageCreateRequest",
                "messageCreateRequest.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        String responseBody = mockMvc.perform(multipart("/api/messages")
                        .file(messageCreateRequestPart))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return UUID.fromString(jsonNode.get("id").asText());
    }
}