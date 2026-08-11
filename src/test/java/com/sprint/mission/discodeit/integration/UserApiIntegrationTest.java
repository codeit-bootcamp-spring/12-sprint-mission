package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.user.Role;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;
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
        // given
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

        // when
        String responseBody = mockMvc.perform(multipart("/api/users")
                        .file(userCreateRequestPart)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.email").value("user1@test.com"))
                .andExpect(jsonPath("$.online").value(false))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(responseBody);
        UUID userId = UUID.fromString(jsonNode.get("id").asText());

        // then
        assertThat(userRepository.findById(userId)).isPresent();
    }

    @Test
    @DisplayName("사용자 생성 API 실패 - 중복 username")
    void createUser_fail_duplicateUsername() throws Exception {
        // given
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

        // when & then
        mockMvc.perform(multipart("/api/users")
                        .file(userCreateRequestPart)
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("사용자 단건 조회 API 통합 테스트")
    void findUserById_success() throws Exception {
        // given
        UUID userId = createUser("user1", "user1@test.com", "password");

        // when & then
        mockMvc.perform(get("/api/users/{userId}", userId)
                        .with(asUser(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.email").value("user1@test.com"));
    }

    @Test
    @DisplayName("사용자 목록 조회 API 통합 테스트")
    void findAllUsers_success() throws Exception {
        // given
        UUID userId1 = createUser("user1", "user1@test.com", "password");
        UUID userId2 = createUser("user2", "user2@test.com", "password");

        // when & then
        mockMvc.perform(get("/api/users")
                        .with(asUser(userId1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '%s')]".formatted(userId1)).exists())
                .andExpect(jsonPath("$[?(@.id == '%s')]".formatted(userId2)).exists());
    }

    @Test
    @DisplayName("사용자 수정 API 통합 테스트")
    void updateUser_success() throws Exception {
        // given
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

        // when
        mockMvc.perform(multipart("/api/users/{userId}", userId)
                        .file(userUpdateRequestPart)
                        .with(csrf())
                        .with(asUser(userId))
                        .with(requestBuilder -> {
                            requestBuilder.setMethod("PATCH");
                            return requestBuilder;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("newUser"))
                .andExpect(jsonPath("$.email").value("new@test.com"));

        // then
        assertThat(userRepository.findById(userId)).isPresent();
        assertThat(userRepository.findById(userId).get().getUsername()).isEqualTo("newUser");
        assertThat(userRepository.findById(userId).get().getEmail()).isEqualTo("new@test.com");
    }

    @Test
    @DisplayName("사용자 삭제 API 통합 테스트")
    void deleteUser_success() throws Exception {
        // given
        UUID userId = createUser("user1", "user1@test.com", "password");

        // when
        mockMvc.perform(delete("/api/users/{userId}", userId)
                        .with(csrf())
                        .with(asUser(userId)))
                .andExpect(status().isNoContent());

        // then
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
                        .file(userCreateRequestPart)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return UUID.fromString(jsonNode.get("id").asText());
    }

    private RequestPostProcessor asUser(UUID userId) {
        UserResponse response = new UserResponse(
                userId, "authenticated-user", "authenticated@test.com",
                null, true, Role.USER
        );
        return user(new DiscodeitUserDetails(response, "password"));
    }
}
