package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReadStatusApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserService userService;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private ReadStatusService readStatusService;

  @Test
  @DisplayName("ReadStatus 생성 성공")
  void createReadStatus_Success() throws Exception {
    UserDto user = userService.create(
        new UserCreateRequest(
            "rsUser",
            "rs@example.com",
            "password123"
        ),
        Optional.empty()
    );

    ChannelDto channel =
        channelService.create(new PublicChannelCreateRequest("rs채널", null));

    ReadStatusCreateRequest request =
        new ReadStatusCreateRequest(
            user.id(),
            channel.id(),
            Instant.now()
        );

    mockMvc.perform(post("/api/readStatuses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.userId", is(user.id().toString())))
        .andExpect(jsonPath("$.channelId", is(channel.id().toString())));
  }

  @Test
  @DisplayName("이미 존재하는 ReadStatus 생성 시 409 반환")
  void createReadStatus_Duplicate_Returns409() throws Exception {
    UserDto user = userService.create(
        new UserCreateRequest(
            "dupRsUser",
            "duprs@example.com",
            "password123"
        ),
        Optional.empty()
    );

    ChannelDto channel =
        channelService.create(new PublicChannelCreateRequest("dup채널", null));

    readStatusService.create(
        new ReadStatusCreateRequest(
            user.id(),
            channel.id(),
            Instant.now()
        )
    );

    ReadStatusCreateRequest request =
        new ReadStatusCreateRequest(
            user.id(),
            channel.id(),
            Instant.now()
        );

    mockMvc.perform(post("/api/readStatuses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("ReadStatus 수정 성공")
  void updateReadStatus_Success() throws Exception {
    UserDto user = userService.create(
        new UserCreateRequest(
            "updateRsUser",
            "updaters@example.com",
            "password123"
        ),
        Optional.empty()
    );

    ChannelDto channel =
        channelService.create(new PublicChannelCreateRequest("update채널", null));

    ReadStatusDto readStatus = readStatusService.create(
        new ReadStatusCreateRequest(
            user.id(),
            channel.id(),
            Instant.now()
        )
    );

    Instant newLastReadAt = Instant.now();
    ReadStatusUpdateRequest request =
        new ReadStatusUpdateRequest(newLastReadAt);

    mockMvc.perform(
            patch("/api/readStatuses/{readStatusId}", readStatus.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(readStatus.id().toString())));
  }

  @Test
  @DisplayName("존재하지 않는 ReadStatus 수정 시 404 반환")
  void updateReadStatus_NotFound_Returns404() throws Exception {
    ReadStatusUpdateRequest request =
        new ReadStatusUpdateRequest(Instant.now());

    mockMvc.perform(
            patch("/api/readStatuses/{readStatusId}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("userId로 ReadStatus 목록 조회 성공")
  void findAllByUserId_Success() throws Exception {
    UserDto user = userService.create(
        new UserCreateRequest(
            "listRsUser",
            "listrs@example.com",
            "password123"
        ),
        Optional.empty()
    );

    ChannelDto channel1 =
        channelService.create(new PublicChannelCreateRequest("채널1", null));
    ChannelDto channel2 =
        channelService.create(new PublicChannelCreateRequest("채널2", null));

    readStatusService.create(
        new ReadStatusCreateRequest(
            user.id(),
            channel1.id(),
            Instant.now()
        )
    );

    readStatusService.create(
        new ReadStatusCreateRequest(
            user.id(),
            channel2.id(),
            Instant.now()
        )
    );

    mockMvc.perform(get("/api/readStatuses")
            .param("userId", user.id().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].userId", is(user.id().toString())));
  }

  @Test
  @DisplayName("ReadStatus 없는 userId 조회 시 빈 목록 반환")
  void findAllByUserId_Empty_ReturnsEmpty() throws Exception {
    UserDto user = userService.create(
        new UserCreateRequest(
            "emptyRsUser",
            "emptyrs@example.com",
            "password123"
        ),
        Optional.empty()
    );

    mockMvc.perform(get("/api/readStatuses")
            .param("userId", user.id().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }
}