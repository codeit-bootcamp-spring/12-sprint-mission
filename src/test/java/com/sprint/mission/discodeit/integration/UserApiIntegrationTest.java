package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.repository.UserRepository;
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
class UserApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 생성 API 통합 테스트")
    void createUser_success() throws Exception {
        UserCreateRequest request = new UserCreateRequest(
                "user1",
                "user1@test.com",
                "password"
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
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.email").value("user1@test.com"))
                .andExpect(jsonPath("$.online").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(responseBody);
        UUID userId = UUID.fromString(jsonNode.get("id").asText());

        assertThat(userRepository.findById(userId)).isPresent();
    }

    @Test
    @DisplayName("사용자 생성 API 실패 - 중복 username")
    void createUser_fail_duplicateUsername() throws Exception {
        createUser("user1", "user1@test.com", "password");

        UserCreateRequest duplicateRequest = new UserCreateRequest(
                "user1",
                "other@test.com",
                "password"
        );

        MockMultipartFile userCreateRequestPart = new MockMultipartFile(
                "userCreateRequest",
                "userCreateRequest.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(duplicateRequest)
        );

        mockMvc.perform(multipart("/api/users")
                        .file(userCreateRequestPart))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("사용자 단건 조회 API 통합 테스트")
    void findUserById_success() throws Exception {
        UUID userId = createUser("user1", "user1@test.com", "password");

        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.email").value("user1@test.com"));
    }

    @Test
    @DisplayName("사용자 목록 조회 API 통합 테스트")
    void findAllUsers_success() throws Exception {
        UUID userId1 = createUser("user1", "user1@test.com", "password");
        UUID userId2 = createUser("user2", "user2@test.com", "password");

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '%s')]".formatted(userId1)).exists())
                .andExpect(jsonPath("$[?(@.id == '%s')]".formatted(userId2)).exists());
    }

    @Test
    @DisplayName("사용자 수정 API 통합 테스트")
    void updateUser_success() throws Exception {
        UUID userId = createUser("user1", "user1@test.com", "password");

        UserUpdateRequest request = new UserUpdateRequest(
                "newUser",
                "new@test.com",
                "newPassword"
        );

        MockMultipartFile userUpdateRequestPart = new MockMultipartFile(
                "userUpdateRequest",
                "userUpdateRequest.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/users/{userId}", userId)
                        .file(userUpdateRequestPart)
                        .with(requestBuilder -> {
                            requestBuilder.setMethod("PATCH");
                            return requestBuilder;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("newUser"))
                .andExpect(jsonPath("$.email").value("new@test.com"));

        assertThat(userRepository.findById(userId)).isPresent();
        assertThat(userRepository.findById(userId).get().getUsername()).isEqualTo("newUser");
        assertThat(userRepository.findById(userId).get().getEmail()).isEqualTo("new@test.com");
    }

    @Test
    @DisplayName("사용자 삭제 API 통합 테스트")
    void deleteUser_success() throws Exception {
        UUID userId = createUser("user1", "user1@test.com", "password");

        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(userId)).isEmpty();
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
}