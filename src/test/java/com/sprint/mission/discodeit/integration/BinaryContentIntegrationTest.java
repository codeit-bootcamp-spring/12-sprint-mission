package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.service.BinaryContentService;
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
class BinaryContentIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private BinaryContentService binaryContentService;

  @Test
  @DisplayName("바이너리 콘텐츠 단건 조회 성공")
  void findBinaryContent_Success() throws Exception {
    UserCreateRequest userRequest =
        new UserCreateRequest("binaryUser", "binary@example.com", "password123");

    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(userRequest)
    );

    MockMultipartFile profilePart = new MockMultipartFile(
        "profile",
        "test.jpg",
        MediaType.IMAGE_JPEG_VALUE,
        "image-data".getBytes()
    );

    String responseBody = mockMvc.perform(multipart("/api/user")
            .file(userPart)
            .file(profilePart))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    String binaryContentId = objectMapper.readTree(responseBody)
        .path("profile")
        .path("id")
        .asText();

    mockMvc.perform(get("/api/binaryContents/{binaryContentId}", binaryContentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(binaryContentId)))
        .andExpect(jsonPath("$.fileName", is("test.jpg")));
  }

  @Test
  @DisplayName("존재하지 않는 바이너리 콘텐츠 조회 시 404 반환")
  void findBinaryContent_NotFound_Returns404() throws Exception {
    mockMvc.perform(get("/api/binaryContents/{binaryContentId}", UUID.randomUUID()))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("ID 목록으로 바이너리 콘텐츠 다건 조회 성공")
  void findAllByIdIn_Success() throws Exception {
    BinaryContentCreateRequest req1 =
        new BinaryContentCreateRequest("file1.jpg", "image/jpeg", "data1".getBytes());
    BinaryContentCreateRequest req2 =
        new BinaryContentCreateRequest("file2.jpg", "image/jpeg", "data2".getBytes());

    BinaryContentDto content1 = binaryContentService.create(req1);
    BinaryContentDto content2 = binaryContentService.create(req2);

    mockMvc.perform(get("/api/binaryContents")
            .param("binaryContentIds", content1.id().toString(), content2.id().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  @DisplayName("존재하지 않는 ID 목록 조회 시 빈 목록 반환")
  void findAllByIdIn_NotFound_ReturnsEmpty() throws Exception {
    mockMvc.perform(get("/api/binaryContents")
            .param("binaryContentIds", UUID.randomUUID().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }
}
