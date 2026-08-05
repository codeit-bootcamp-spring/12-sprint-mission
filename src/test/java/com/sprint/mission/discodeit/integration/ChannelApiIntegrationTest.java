package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.TestSecuritySupport;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class ChannelApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserService userService;

  @Autowired
  private ChannelService channelService;

  private UserDto userDto;
  private ChannelDto publicChannelDto;
  private ChannelDto privateChannelDto;

  @BeforeEach
  public void setup() {
    userDto = userService.create(
        new UserCreateRequest("test_user", "test@test.com", "password1!"),
        Optional.empty()
    );
    TestSecuritySupport.authenticate(userDto.id(), Role.CHANNEL_MANAGER);

    publicChannelDto = channelService.create(
        new PublicChannelCreateRequest("channel", "description")
    );

    privateChannelDto = channelService.create(
        new PrivateChannelCreateRequest(
            List.of(userDto.id())
        )
    );
  }

  @AfterEach
  void tearDown() {
    TestSecuritySupport.clear();
  }

  @Test
  @DisplayName("create_public_channel")
  public void create_public_channel() throws Exception {
    PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest(
        "public_channel",
        "description"
    );

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(publicChannelCreateRequest)))
        .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.toString()))
        .andExpect(jsonPath("$.name").value(publicChannelCreateRequest.name()))
        .andExpect(jsonPath("$.description").value(publicChannelCreateRequest.description()))
        .andExpect(jsonPath("$.lastMessageAt").doesNotExist());
  }

  @Test
  @DisplayName("create_private_channel")
  public void create_private_channel() throws Exception {
    PrivateChannelCreateRequest privateChannelCreateRequest = new PrivateChannelCreateRequest(
        List.of(userDto.id())
    );

    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(privateChannelCreateRequest)))
        .andExpect(jsonPath("$.type").value(ChannelType.PRIVATE.toString()))
        .andExpect(jsonPath("$.participants[0].username").value(userDto.username()));
  }

  @Test
  @DisplayName("find_all_by_user_id")
  public void find_all_by_user_id() throws Exception {
    mockMvc.perform(get("/api/channels")
            .param("userId", String.valueOf(userDto.id())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("$[*].id").value(hasItems(publicChannelDto.id().toString())));
  }

  @Test
  @DisplayName("update_public_channel")
  public void update_public_channel() throws Exception {
    ChannelUpdateRequest channelUpdateRequest = new ChannelUpdateRequest(
        "newName",
        "newDescription"
    );

    mockMvc.perform(patch("/api/channels/{channelId}", publicChannelDto.id())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(channelUpdateRequest)))
        .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.toString()))
        .andExpect(jsonPath("$.name").value(channelUpdateRequest.newName()))
        .andExpect(jsonPath("$.description").value(channelUpdateRequest.newDescription()))
        .andExpect(jsonPath("$.lastMessageAt").doesNotExist());
  }

  @Test
  @DisplayName("delete_channel")
  public void delete_channel() throws Exception {
    mockMvc.perform(get("/api/channels")
            .param("userId", String.valueOf(userDto.id())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2));

    mockMvc.perform(delete("/api/channels/{channelId}", publicChannelDto.id()))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/channels")
            .param("userId", String.valueOf(userDto.id())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  @DisplayName("find_read_statuses_by_user_id")
  void findReadStatusesByUserId() throws Exception {
    mockMvc.perform(get("/api/readStatuses")
            .param("userId", userDto.id().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].userId").value(userDto.id().toString()))
        .andExpect(jsonPath("$[0].channelId").value(privateChannelDto.id().toString()));
  }
}
