package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private MessageMapper messageMapper;
  @Mock
  private BinaryContentStorage binaryContentStorage;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private PageResponseMapper pageResponseMapper;

  @InjectMocks
  private BasicMessageService messageService;

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

    channel = new Channel(ChannelType.PUBLIC, "테스트채널", null);
    ReflectionTestUtils.setField(channel, "id", channelId);

    author = new User("testUser", "test@example.com", "test1234", null);
    ReflectionTestUtils.setField(author, "id", authorId);

    message = new Message("안녕하세요", channel, author, List.of());
    ReflectionTestUtils.setField(message, "id", messageId);

    messageDto = new MessageDto(messageId, Instant.now(), Instant.now(), "안녕하세요", channelId, null,
        List.of());
  }

  // ── create ──────────────────────────────────────────────

  @Test
  @DisplayName("메시지 생성 성공")
  void createMessage_Success() {
    // given
    MessageCreateRequest request = new MessageCreateRequest("안녕하세요", channelId, authorId);
    given(channelRepository.findById(eq(channelId))).willReturn(Optional.of(channel));
    given(userRepository.findById(eq(authorId))).willReturn(Optional.of(author));
    given(messageRepository.save(any(Message.class))).willReturn(message);
    given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

    // when
    MessageDto result = messageService.create(request, List.of());

    // then
    assertThat(result.content()).isEqualTo("안녕하세요");
    verify(messageRepository).save(any(Message.class));
  }

  @Test
  @DisplayName("존재하지 않는 채널에 메시지 생성 시 실패")
  void createMessage_WithNonExistentChannel_ThrowsException() {
    // given
    MessageCreateRequest request = new MessageCreateRequest("안녕하세요", channelId, authorId);
    given(channelRepository.findById(eq(channelId))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("존재하지 않는 사용자로 메시지 생성 시 실패")
  void createMessage_WithNonExistentAuthor_ThrowsException() {
    // given
    MessageCreateRequest request = new MessageCreateRequest("안녕하세요", channelId, authorId);
    given(channelRepository.findById(eq(channelId))).willReturn(Optional.of(channel));
    given(userRepository.findById(eq(authorId))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(UserNotFoundException.class);
  }

  // ── update ──────────────────────────────────────────────

  @Test
  @DisplayName("메시지 수정 성공")
  void updateMessage_Success() {
    // given
    MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용");
    given(messageRepository.findById(eq(messageId))).willReturn(Optional.of(message));
    given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);

    // when
    MessageDto result = messageService.update(messageId, request);

    // then
    assertThat(result).isEqualTo(messageDto);
  }

  @Test
  @DisplayName("존재하지 않는 메시지 수정 시 실패")
  void updateMessage_WithNonExistentId_ThrowsException() {
    // given
    MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용");
    given(messageRepository.findById(eq(messageId))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.update(messageId, request))
        .isInstanceOf(MessageNotFoundException.class);
  }

  // ── delete ──────────────────────────────────────────────

  @Test
  @DisplayName("메시지 삭제 성공")
  void deleteMessage_Success() {
    // given
    given(messageRepository.existsById(eq(messageId))).willReturn(true);

    // when
    messageService.delete(messageId);

    // then
    verify(messageRepository).deleteById(messageId);
  }

  @Test
  @DisplayName("존재하지 않는 메시지 삭제 시 실패")
  void deleteMessage_WithNonExistentId_ThrowsException() {
    // given
    given(messageRepository.existsById(eq(messageId))).willReturn(false);

    // when & then
    assertThatThrownBy(() -> messageService.delete(messageId))
        .isInstanceOf(MessageNotFoundException.class);
  }

  // ── findAllByChannelId ───────────────────────────────────

  @Test
  @DisplayName("채널 메시지 목록 조회 성공")
  void findAllByChannelId_Success() {
    // given
    Instant cursor = Instant.now();
    PageRequest pageable = PageRequest.of(0, 50);
    SliceImpl<Message> slice = new SliceImpl<>(List.of(message), pageable, false);
    PageResponse<MessageDto> pageResponse = new PageResponse<>(List.of(messageDto), cursor, 1,
        false, null);

    given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), any(Instant.class),
        eq(pageable)))
        .willReturn(slice);
    given(messageMapper.toDto(any(Message.class))).willReturn(messageDto);
    given(pageResponseMapper.<MessageDto>fromSlice(any(), any())).willReturn(pageResponse);

    // when
    PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, cursor,
        pageable);

    // then
    assertThat(result.content()).hasSize(1);
  }

  @Test
  @DisplayName("메시지 없는 채널 조회 시 빈 목록 반환")
  void findAllByChannelId_EmptyChannel_ReturnsEmpty() {
    // given
    PageRequest pageable = PageRequest.of(0, 50);
    SliceImpl<Message> slice = new SliceImpl<>(List.of(), pageable, false);
    PageResponse<MessageDto> pageResponse = new PageResponse<>(List.of(), null, 0, false, null);

    given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), any(Instant.class),
        eq(pageable)))
        .willReturn(slice);
    given(pageResponseMapper.<MessageDto>fromSlice(any(), any())).willReturn(pageResponse);

    // when
    PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, null, pageable);

    // then
    assertThat(result.content()).isEmpty();
  }
}