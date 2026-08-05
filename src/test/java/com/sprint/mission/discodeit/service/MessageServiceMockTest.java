package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.page.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
public class MessageServiceMockTest {

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @Mock
  private MessageMapper messageMapper;

  @Mock
  private BinaryContentMapper binaryContentMapper;

  @Mock
  private PageResponseMapper pageResponseMapper;

  @InjectMocks
  private BasicMessageService messageService;

  private UUID messageId;
  private UUID channelId;
  private UUID authorId;

  private MessageCreateRequest messageCreateRequest;
  private MessageUpdateRequest messageUpdateRequest;
  private BinaryContentCreateRequest attachmentCreateRequest;

  private Channel channel;
  private User author;
  private BinaryContent attachment;
  private Message message;
  private MessageDto messageDto;

  @BeforeEach
  void setUp() {
    messageId = UUID.randomUUID();
    channelId = UUID.randomUUID();
    authorId = UUID.randomUUID();

    messageCreateRequest = new MessageCreateRequest(
        channelId,
        authorId,
        "hello"
    );

    messageUpdateRequest = new MessageUpdateRequest(
        "updated content"
    );

    byte[] attachmentBytes = "file-content".getBytes(StandardCharsets.UTF_8);

    attachmentCreateRequest = new BinaryContentCreateRequest(
        "test.txt",
        "text/plain",
        attachmentBytes
    );

    channel = Channel.builder()
        .id(channelId)
        .name("public-channel")
        .description("description")
        .type(ChannelType.PUBLIC)
        .build();

    author = User.builder()
        .id(authorId)
        .username("user1")
        .email("user1@test.com")
        .password("password")
        .build();

    attachment = BinaryContent.builder()
        .id(UUID.randomUUID())
        .fileName(attachmentCreateRequest.fileName())
        .contentType(attachmentCreateRequest.contentType())
        .size((long) attachmentCreateRequest.bytes().length)
        .build();

    message = Message.builder()
        .id(messageId)
        .content(messageCreateRequest.content())
        .channel(channel)
        .author(author)
        .attachments(List.of(attachment))
        .build();

    messageDto = new MessageDto(
        messageId,
        Instant.now(),
        Instant.now(),
        message.getContent(),
        channelId,
        null,
        List.of()
    );
  }

  @Test
  @DisplayName("message_create_success")
  void message_create_success() {
    given(channelRepository.findById(any())).willReturn(Optional.of(channel));
    given(userRepository.findDetailById(any())).willReturn(Optional.of(author));
    given(binaryContentMapper.toEntity(any())).willReturn(attachment);
    given(binaryContentRepository.save(any())).willReturn(attachment);
//    given(binaryContentStorage.put(any(UUID.class), any(byte[].class)));
    given(messageMapper.toEntity(messageCreateRequest, channel, author, List.of(attachment)))
        .willReturn(message);
    given(messageRepository.save(any())).willReturn(message);
    given(messageMapper.toDto(any())).willReturn(messageDto);

    MessageDto result = messageService.create(
        messageCreateRequest,
        List.of(attachmentCreateRequest)
    );

    assertNotNull(result);
    assertEquals(messageDto, result);

    verify(messageRepository, times(1)).save(message);
    verify(binaryContentStorage, times(1))
        .put(
            eq(attachment.getId()),
            any(byte[].class),
            eq(attachmentCreateRequest.contentType())
        );
  }

  @Test
  @DisplayName("message_create_failed")
  void message_create_failed() {
    given(channelRepository.findById(any())).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.create(messageCreateRequest, List.of()))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("message_findByChannelId_success")
  void message_findByChannelId_success() {
    Pageable pageable = PageRequest.of(0, 10);

    Slice<UUID> messageIdSlice = new SliceImpl<>(List.of(messageId), pageable, false);

    PageResponse<MessageDto> pageResponse = new PageResponse<>(
        List.of(messageDto),
        null,
        pageable.getPageSize(),
        false,
        null
    );

    given(messageRepository.findIdsByChannelId(channelId, pageable))
        .willReturn(messageIdSlice);
    given(messageRepository.findAllDetailByIdIn(List.of(messageId)))
        .willReturn(List.of(message));
    given(messageMapper.toDto(any())).willReturn(messageDto);
    given(pageResponseMapper.fromSlice(List.of(messageDto), null, messageIdSlice))
        .willReturn(pageResponse);

    PageResponse<MessageDto> result = messageService.findAllByChannelId(
        channelId,
        null,
        pageable
    );

    assertEquals(pageResponse, result);
  }

  @Test
  @DisplayName("message_findByChannelId_failed")
  void message_findByChannelId_failed() {
    Pageable pageable = PageRequest.of(0, 10);

    assertThatThrownBy(() -> messageService.findAllByChannelId(null, null, pageable))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("message_update_success")
  void message_update_success() {
    given(messageRepository.findDetailById(any())).willReturn(Optional.of(message));
    given(messageRepository.save(any(Message.class))).willAnswer(inv -> inv.getArgument(0));
    given(messageMapper.toDto(any())).willReturn(messageDto);

    MessageDto result = messageService.update(
        messageId,
        messageUpdateRequest,
        List.of()
    );

    assertThat(result).isEqualTo(messageDto);
    assertEquals(messageUpdateRequest.newContent(), message.getContent());
  }

  @Test
  @DisplayName("message_update_failed")
  void message_update_failed() {
    given(messageRepository.findDetailById(any())).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.update(messageId, messageUpdateRequest, List.of()))
        .isInstanceOf(MessageNotFoundException.class);
  }

  @Test
  @DisplayName("message_delete_success")
  void message_delete_success() {
    given(messageRepository.findDetailById(any())).willReturn(Optional.of(message));

    messageService.delete(messageId);

    verify(messageRepository, times(1)).delete(message);
  }

  @Test
  @DisplayName("message_delete_failed")
  void message_delete_failed() {
    given(messageRepository.findDetailById(any())).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.delete(messageId))
        .isInstanceOf(MessageNotFoundException.class);
  }
}
