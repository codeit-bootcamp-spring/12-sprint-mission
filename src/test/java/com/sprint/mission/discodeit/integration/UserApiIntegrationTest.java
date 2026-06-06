package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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

  @MockitoBean
  private BinaryContentStorage binaryContentStorage;

  // ── create ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("사용자 생성 성공 - 201")
  void create() throws Exception {
    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new UserCreateRequest("integUser", "integ@test.com", "password1234!")));

    mockMvc.perform(multipart("/api/users")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("integUser"))
        .andExpect(jsonPath("$.email").value("integ@test.com"));
  }

  @Test
  @DisplayName("사용자 생성 실패 - 409 (중복 이메일)")
  void create_duplicateEmail() throws Exception {
    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new UserCreateRequest("dupUser", "dup@test.com", "password1234!")));

    mockMvc.perform(multipart("/api/users")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());

    MockMultipartFile duplicatePart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new UserCreateRequest("dupUser2", "dup@test.com", "password1234!")));

    mockMvc.perform(multipart("/api/users")
            .file(duplicatePart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isConflict());
  }

  // ── update ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("사용자 수정 성공 - 200")
  void update() throws Exception {
    String userId = createUser("updateUser", "update@test.com");

    MockMultipartFile updatePart = new MockMultipartFile(
        "userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new UserUpdateRequest("updatedName", null, null)));

    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(updatePart)
            .with(req -> {
              req.setMethod("PATCH");
              return req;
            })
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("updatedName"));
  }

  @Test
  @DisplayName("사용자 수정 실패 - 404 (존재하지 않는 사용자)")
  void update_notFound() throws Exception {
    MockMultipartFile updatePart = new MockMultipartFile(
        "userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new UserUpdateRequest("ghost", null, null)));

    mockMvc.perform(multipart("/api/users/{userId}", UUID.randomUUID())
            .file(updatePart)
            .with(req -> {
              req.setMethod("PATCH");
              return req;
            })
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isNotFound());
  }

  // ── delete ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("사용자 삭제 성공 - 204")
  void deleteUser() throws Exception {
    String userId = createUser("deleteUser", "delete@test.com");

    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("사용자 삭제 실패 - 404 (존재하지 않는 사용자)")
  void delete_notFound() throws Exception {
    mockMvc.perform(delete("/api/users/{userId}", UUID.randomUUID()))
        .andExpect(status().isNotFound());
  }

  // ── findAll ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("사용자 목록 조회 성공 - 200")
  void findAll() throws Exception {
    createUser("listUser1", "list1@test.com");
    createUser("listUser2", "list2@test.com");

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  @DisplayName("사용자 목록 조회 성공 - 빈 목록")
  void findAll_emptyList() throws Exception {
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  // ── helper ──────────────────────────────────────────────────────────────

  private String createUser(String username, String email) throws Exception {
    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new UserCreateRequest(username, email, "password1234!")));

    MvcResult result = mockMvc.perform(multipart("/api/users")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andReturn();

    return objectMapper.readTree(result.getResponse().getContentAsString())
        .get("id").asText();
  }
}
