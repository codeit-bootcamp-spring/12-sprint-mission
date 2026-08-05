package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.TestSecuritySupport;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class MessageApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserService userService;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private MessageService messageService;

  private UserDto userDto;
  private ChannelDto channelDto;
  private MessageDto messageDto;

  @BeforeEach
  void setUp() {
    userDto = userService.create(
        new UserCreateRequest("test_user", "test@test.com", "password1!"),
        Optional.empty()
    );
    TestSecuritySupport.authenticate(userDto.id(), Role.CHANNEL_MANAGER);

    channelDto = channelService.create(
        new PublicChannelCreateRequest("test_channel", "description")
    );

    messageDto = messageService.create(
        new MessageCreateRequest(channelDto.id(), userDto.id(), "content")
        , List.of()
    );
  }

  @AfterEach
  void tearDown() {
    TestSecuritySupport.clear();
  }

  @Test
  @DisplayName("create_message")
  void createMessage() throws Exception {
    MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
        channelDto.id(),
        userDto.id(),
        "content"
    );

    MockMultipartFile messageCreateRequestPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(messageCreateRequest)
    );

    MockMultipartFile attachments = new MockMultipartFile(
        "attachments",
        "attachments.png",
        "image/png",
        "attachments".getBytes()
    );

    mockMvc.perform(multipart("/api/messages")
            .file(messageCreateRequestPart)
            .file(attachments))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value(messageCreateRequest.content()))
        .andExpect(jsonPath("$.channelId").value(messageCreateRequest.channelId().toString()))
        .andExpect(jsonPath("$.author.id").value(messageCreateRequest.authorId().toString()))
        .andExpect(jsonPath("$.attachments[0].fileName").value(attachments.getOriginalFilename()))
        .andExpect(jsonPath("$.attachments[0].size").value(attachments.getSize()))
        .andExpect(jsonPath("$.attachments[0].contentType").value(attachments.getContentType()));
  }

  @Test
  @DisplayName("find_all_by_channel_id")
  void findAllByChannelId() throws Exception {

    MessageDto messageDto1 = messageService.create(
        new MessageCreateRequest(channelDto.id(), userDto.id(), "content")
        , List.of()
    );

    Pageable pageable = PageRequest.of(0, 10);

    mockMvc.perform(get("/api/messages")
            .param("channelId", channelDto.id().toString())
            .param("size", String.valueOf(pageable.getPageSize())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()").value(2))
        .andExpect(jsonPath("$.content[0].content").value(messageDto.content()))
        .andExpect(jsonPath("$.content[0].channelId").value(messageDto.channelId().toString()))
        .andExpect(jsonPath("$.content[0].author.id").value(messageDto.author().id().toString()))
        .andExpect(jsonPath("$.content[0].author.username").value(messageDto.author().username()))
        .andExpect(jsonPath("$.content[0].attachments.size()").value(0))
        .andExpect(jsonPath("$.content[1].content").value(messageDto1.content()))
        .andExpect(jsonPath("$.content[1].channelId").value(messageDto1.channelId().toString()))
        .andExpect(jsonPath("$.content[1].author.id").value(messageDto1.author().id().toString()))
        .andExpect(jsonPath("$.content[1].author.username").value(messageDto1.author().username()))
        .andExpect(jsonPath("$.content[1].attachments.size()").value(0))
        .andExpect(jsonPath("$.nextCursor").doesNotExist())
        .andExpect(jsonPath("$.size").value(pageable.getPageSize()))
        .andExpect(jsonPath("$.hasNext").value(false))
        .andExpect(jsonPath("$.totalElements").doesNotExist());
  }

  @Test
  @DisplayName("update_message")
  void updateMessage() throws Exception {
    MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest(
        "newContent"
    );

    MockMultipartFile messageUpdateRequestPart = new MockMultipartFile(
        "messageUpdateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(messageUpdateRequest)
    );

    mockMvc.perform(multipart("/api/messages/{messageId}", messageDto.id())
            .file(messageUpdateRequestPart)
            .with(request -> {
              request.setMethod("PATCH");
              return request;
            }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(messageDto.id().toString()))
        .andExpect(jsonPath("$.content").value(messageUpdateRequest.newContent()))
        .andExpect(jsonPath("$.channelId").value(messageDto.channelId().toString()))
        .andExpect(jsonPath("$.author.id").value(messageDto.author().id().toString()))
        .andExpect(jsonPath("$.author.username").value(messageDto.author().username()));
  }

  @Test
  @DisplayName("update_message_forbidden_when_not_author")
  void updateMessage_forbidden_when_not_author() throws Exception {
    UserDto otherUserDto = userService.create(
        new UserCreateRequest("other_user", "other@test.com", "password2!"),
        Optional.empty()
    );
    TestSecuritySupport.authenticate(otherUserDto.id(), Role.USER);

    MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest(
        "newContent"
    );

    MockMultipartFile messageUpdateRequestPart = new MockMultipartFile(
        "messageUpdateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(messageUpdateRequest)
    );

    mockMvc.perform(multipart("/api/messages/{messageId}", messageDto.id())
            .file(messageUpdateRequestPart)
            .with(request -> {
              request.setMethod("PATCH");
              return request;
            }))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("update_message_with_attachments")
  void updateMessage_with_attachments() throws Exception {
    MessageCreateRequest beforeMessageCreateRequest = new MessageCreateRequest(
        channelDto.id(),
        userDto.id(),
        "content"
    );

    MockMultipartFile beforeMessageCreateRequestPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(beforeMessageCreateRequest)
    );

    MockMultipartFile attachments = new MockMultipartFile(
        "attachments",
        "before_attachments.png",
        "image/png",
        "before_attachments".getBytes()
    );

    String createResponse = mockMvc.perform(multipart("/api/messages")
            .file(beforeMessageCreateRequestPart)
            .file(attachments))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value(beforeMessageCreateRequest.content()))
        .andExpect(jsonPath("$.channelId").value(beforeMessageCreateRequest.channelId().toString()))
        .andExpect(jsonPath("$.author.id").value(beforeMessageCreateRequest.authorId().toString()))
        .andExpect(jsonPath("$.attachments[0].fileName").value(attachments.getOriginalFilename()))
        .andExpect(jsonPath("$.attachments[0].size").value(attachments.getSize()))
        .andExpect(jsonPath("$.attachments[0].contentType").value(attachments.getContentType()))
        .andReturn()
        .getResponse()
        .getContentAsString();

    MessageDto beforeMessageDto = objectMapper.readValue(createResponse, MessageDto.class);

    MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest(
        "newContent"
    );

    MockMultipartFile messageUpdateRequestPart = new MockMultipartFile(
        "messageUpdateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(messageUpdateRequest)
    );

    MockMultipartFile upAttachments = new MockMultipartFile(
        "attachments",
        "after_attachments.txt",
        "text/plain",
        "after_attachments".getBytes()
    );

    mockMvc.perform(multipart("/api/messages/{messageId}", beforeMessageDto.id())
            .file(messageUpdateRequestPart)
            .file(upAttachments)
            .with(request -> {
              request.setMethod("PATCH");
              return request;
            }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(beforeMessageDto.id().toString()))
        .andExpect(jsonPath("$.content").value(messageUpdateRequest.newContent()))
        .andExpect(jsonPath("$.attachments.size()").value(1))
        .andExpect(jsonPath("$.attachments[0].fileName").value(upAttachments.getOriginalFilename()))
        .andExpect(jsonPath("$.attachments[0].contentType").value(upAttachments.getContentType()))
        .andExpect(jsonPath("$.attachments[0].size").value(upAttachments.getBytes().length));
  }

  @Test
  @DisplayName("delete_message")
  void deleteMessage() throws Exception {
    mockMvc.perform(get("/api/messages")
            .param("channelId", channelDto.id().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()").value(1));

    mockMvc.perform(delete("/api/messages/{userId}", messageDto.id()))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/messages")
            .param("channelId", channelDto.id().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()").value(0));
  }

  @Test
  @DisplayName("delete_message_forbidden_when_not_author")
  void deleteMessage_forbidden_when_not_author() throws Exception {
    UserDto otherUserDto = userService.create(
        new UserCreateRequest("other_user", "other@test.com", "password2!"),
        Optional.empty()
    );
    TestSecuritySupport.authenticate(otherUserDto.id(), Role.USER);

    mockMvc.perform(delete("/api/messages/{messageId}", messageDto.id()))
        .andExpect(status().isForbidden());
  }
}
