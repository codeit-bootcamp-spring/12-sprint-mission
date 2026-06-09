package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

  @Mock MessageRepository messageRepository;
  @Mock ChannelRepository channelRepository;
  @Mock UserRepository userRepository;
  @Mock BinaryContentRepository binaryContentRepository;
  @Mock BinaryContentStorage binaryContentStorage;
  @Mock MessageMapper messageMapper;
  @Mock PageResponseMapper pageResponseMapper;

  @InjectMocks BasicMessageService messageService;

  private UUID messageId;
  private UUID channelId;
  private UUID authorId;
  private Channel channel;
  private User author;
  private Message message;
  private MessageDto messageDto;

  @BeforeEach
  void setUp() {
    messageId = UUID.randomUUID();
    channelId = UUID.randomUUID();
    authorId = UUID.randomUUID();
    channel = new Channel(ChannelType.PUBLIC, "general", null);
    author = new User("user1", "user1@email.com", "pass", null);
    message = new Message("Hello", channel, author, List.of());
    messageDto = new MessageDto(messageId, Instant.now(), Instant.now(),
        "Hello", channelId, null, List.of());
  }

  // ── create ────────────────────────────────────────────────────

  @Test
  @DisplayName("메시지 생성 성공")
  void create_success() {
    // given
    MessageCreateRequest req = new MessageCreateRequest("Hello", channelId, authorId);
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(authorId)).willReturn(Optional.of(author));
    given(messageRepository.save(any(Message.class))).willReturn(message);
    given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

    // when
    MessageDto result = messageService.create(req, List.of());

    // then
    assertThat(result).isNotNull();
    assertThat(result.content()).isEqualTo("Hello");
    then(messageRepository).should().save(any(Message.class));
  }

  @Test
  @DisplayName("채널이 없으면 ChannelNotFoundException 발생")
  void create_channelNotFound_throwsException() {
    // given
    MessageCreateRequest req = new MessageCreateRequest("Hello", channelId, authorId);
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    // when / then
    assertThatThrownBy(() -> messageService.create(req, List.of()))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("작성자가 없으면 UserNotFoundException 발생")
  void create_authorNotFound_throwsException() {
    // given
    MessageCreateRequest req = new MessageCreateRequest("Hello", channelId, authorId);
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(authorId)).willReturn(Optional.empty());

    // when / then
    assertThatThrownBy(() -> messageService.create(req, List.of()))
        .isInstanceOf(UserNotFoundException.class);
  }

  // ── update ────────────────────────────────────────────────────

  @Test
  @DisplayName("메시지 수정 성공")
  void update_success() {
    // given
    MessageUpdateRequest req = new MessageUpdateRequest("Updated");
    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    given(messageRepository.save(any(Message.class))).willReturn(message);
    given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

    // when
    MessageDto result = messageService.update(messageId, req);

    // then
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("존재하지 않는 메시지 수정 시 MessageNotFoundException 발생")
  void update_notFound_throwsException() {
    // given
    MessageUpdateRequest req = new MessageUpdateRequest("Updated");
    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    // when / then
    assertThatThrownBy(() -> messageService.update(messageId, req))
        .isInstanceOf(MessageNotFoundException.class);
  }

  // ── delete ────────────────────────────────────────────────────

  @Test
  @DisplayName("메시지 삭제 성공")
  void delete_success() {
    // given
    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

    // when
    messageService.delete(messageId);

    // then
    then(messageRepository).should().delete(message);
  }

  @Test
  @DisplayName("존재하지 않는 메시지 삭제 시 MessageNotFoundException 발생")
  void delete_notFound_throwsException() {
    // given
    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    // when / then
    assertThatThrownBy(() -> messageService.delete(messageId))
        .isInstanceOf(MessageNotFoundException.class);
  }

  // ── findAllByChannelId ────────────────────────────────────────

  @Test
  @DisplayName("채널 메시지 페이지 조회 성공 - 첫 페이지(cursor=null)")
  void findAllByChannelId_firstPage_success() {
    // given
    SliceImpl<Message> slice = new SliceImpl<>(List.of(message),
        PageRequest.of(0, 50), false);
    PageResponse<MessageDto> expected = new PageResponse<>(List.of(messageDto), null, 50, false, null);

    given(messageRepository.findByChannelIdWithCursor(channelId, null, PageRequest.of(0, 50)))
        .willReturn(slice);
    given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);
    // 제네릭 메서드는 doReturn으로 타입 소거 문제 우회
    org.mockito.Mockito.doReturn(expected).when(pageResponseMapper).fromSlice(any(), any());

    // when
    PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, null, 50);

    // then
    assertThat(result.content()).hasSize(1);
    assertThat(result.hasNext()).isFalse();
  }

  @Test
  @DisplayName("채널 메시지 페이지 조회 - 다음 페이지 있음")
  void findAllByChannelId_hasNextPage() {
    // given
    Instant cursor = Instant.now();
    SliceImpl<Message> slice = new SliceImpl<>(List.of(message),
        PageRequest.of(0, 2), true);
    PageResponse<MessageDto> expected = new PageResponse<>(List.of(messageDto), cursor, 2, true, null);

    given(messageRepository.findByChannelIdWithCursor(channelId, cursor, PageRequest.of(0, 2)))
        .willReturn(slice);
    given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);
    org.mockito.Mockito.doReturn(expected).when(pageResponseMapper).fromSlice(any(), any());

    // when
    PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, cursor, 2);

    // then
    assertThat(result.hasNext()).isTrue();
  }
}
