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
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
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
    @DisplayName("PUBLIC 채널 생성 API 통합 테스트")
    void createPublicChannel_success() throws Exception {
        CreatePublicChannelRequest request = new CreatePublicChannelRequest(
                "public-channel",
                "public-description"
        );

        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.type").value("PUBLIC"))
                .andExpect(jsonPath("$.name").value("public-channel"))
                .andExpect(jsonPath("$.description").value("public-description"));
    }

    @Test
    @DisplayName("PUBLIC 채널 생성 API 실패 - 요청 값 검증 실패")
    void createPublicChannel_fail_invalidRequest() throws Exception {
        CreatePublicChannelRequest request = new CreatePublicChannelRequest(
                "",
                "public-description"
        );

        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("PRIVATE 채널 생성 API 통합 테스트")
    void createPrivateChannel_success() throws Exception {
        UUID userId1 = createUser("user1", "user1@test.com", "password");
        UUID userId2 = createUser("user2", "user2@test.com", "password");

        CreatePrivateChannelRequest request = new CreatePrivateChannelRequest(
                List.of(userId1, userId2)
        );

        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.type").value("PRIVATE"))
                .andExpect(jsonPath("$.participants.length()").value(2))
                .andExpect(jsonPath("$.participants[0].id").exists())
                .andExpect(jsonPath("$.participants[1].id").exists());
    }

    @Test
    @DisplayName("PRIVATE 채널 생성 API 실패 - 존재하지 않는 사용자")
    void createPrivateChannel_fail_userNotFound() throws Exception {
        UUID unknownUserId = UUID.randomUUID();

        CreatePrivateChannelRequest request = new CreatePrivateChannelRequest(
                List.of(unknownUserId)
        );

        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("사용자 ID로 채널 목록 조회 API 통합 테스트")
    void findAllByUserId_success() throws Exception {
        UUID userId1 = createUser("user1", "user1@test.com", "password");
        UUID userId2 = createUser("user2", "user2@test.com", "password");

        UUID publicChannelId = createPublicChannel("public-channel", "public-description");

        CreatePrivateChannelRequest privateRequest = new CreatePrivateChannelRequest(
                List.of(userId1, userId2)
        );

        String privateResponseBody = mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(privateRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID privateChannelId = UUID.fromString(
                objectMapper.readTree(privateResponseBody).get("id").asText()
        );

        mockMvc.perform(get("/api/channels")
                        .param("userId", userId1.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '%s')]".formatted(publicChannelId)).exists())
                .andExpect(jsonPath("$[?(@.id == '%s')]".formatted(privateChannelId)).exists());
    }

    @Test
    @DisplayName("채널 수정 API 통합 테스트")
    void updateChannel_success() throws Exception {
        UUID channelId = createPublicChannel("public-channel", "public-description");

        ChannelUpdateRequest request = new ChannelUpdateRequest(
                "new-channel",
                "new-description"
        );

        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.type").value("PUBLIC"))
                .andExpect(jsonPath("$.name").value("new-channel"))
                .andExpect(jsonPath("$.description").value("new-description"));
    }

    @Test
    @DisplayName("PRIVATE 채널 수정 API 실패")
    void updatePrivateChannel_fail() throws Exception {
        UUID userId1 = createUser("user1", "user1@test.com", "password");
        UUID userId2 = createUser("user2", "user2@test.com", "password");

        CreatePrivateChannelRequest privateRequest = new CreatePrivateChannelRequest(
                List.of(userId1, userId2)
        );

        String responseBody = mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(privateRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID privateChannelId = UUID.fromString(
                objectMapper.readTree(responseBody).get("id").asText()
        );

        ChannelUpdateRequest updateRequest = new ChannelUpdateRequest(
                "new-name",
                "new-description"
        );

        mockMvc.perform(patch("/api/channels/{channelId}", privateChannelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("채널 삭제 API 통합 테스트")
    void deleteChannel_success() throws Exception {
        UUID channelId = createPublicChannel("public-channel", "public-description");

        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"))
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
}