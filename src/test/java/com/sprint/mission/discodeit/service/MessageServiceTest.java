package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

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
  private PageResponseMapper pageResponseMapper;

  @InjectMocks
  private BasicMessageService messageService;

  @Test
  @DisplayName("메시지 생성 성공")
  void create_success() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    MessageCreateRequest request = new MessageCreateRequest(
        "안녕하세요",
        channelId,
        authorId
    );

    BinaryContentCreateRequest attachmentRequest = new BinaryContentCreateRequest(
        "test.txt",
        "text/plain",
        "hello".getBytes()
    );

    Channel channel = new Channel(ChannelType.PUBLIC, "공지", "공지 채널");
    User author = new User("minji", "minji@test.com", "1234", null);
    MessageDto response = mock(MessageDto.class);

    given(channelRepository.findById(channelId))
        .willReturn(Optional.of(channel));

    given(userRepository.findById(authorId))
        .willReturn(Optional.of(author));

    given(binaryContentRepository.save(any(BinaryContent.class)))
        .willAnswer(invocation -> invocation.getArgument(0));

    given(messageRepository.save(any(Message.class)))
        .willAnswer(invocation -> invocation.getArgument(0));

    given(messageMapper.toDto(any(Message.class)))
        .willReturn(response);

    // when
    MessageDto result = messageService.create(request, List.of(attachmentRequest));

    // then
    assertThat(result).isSameAs(response);

    then(channelRepository).should().findById(channelId);
    then(userRepository).should().findById(authorId);
    then(binaryContentRepository).should().save(any(BinaryContent.class));
    then(binaryContentStorage).should()
        .put(org.mockito.ArgumentMatchers.nullable(UUID.class), any(byte[].class));
    then(messageRepository).should().save(any(Message.class));
    then(messageMapper).should().toDto(any(Message.class));
  }

  @Test
  @DisplayName("메시지 생성 실패 - 채널 없음")
  void create_fail_channelNotFound() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    MessageCreateRequest request = new MessageCreateRequest(
        "안녕하세요",
        channelId,
        authorId
    );

    given(channelRepository.findById(channelId))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(ChannelNotFoundException.class);

    then(channelRepository).should().findById(channelId);
    then(userRepository).should(never()).findById(authorId);
    then(messageRepository).should(never()).save(any(Message.class));
  }

  @Test
  @DisplayName("메시지 수정 성공")
  void update_success() {
    // given
    UUID messageId = UUID.randomUUID();

    MessageUpdateRequest request = new MessageUpdateRequest("수정된 메시지");

    Channel channel = new Channel(ChannelType.PUBLIC, "공지", "공지 채널");
    User author = new User("minji", "minji@test.com", "1234", null);
    Message message = new Message("기존 메시지", channel, author, List.of());

    MessageDto response = mock(MessageDto.class);

    given(messageRepository.findById(messageId))
        .willReturn(Optional.of(message));

    given(messageMapper.toDto(message))
        .willReturn(response);

    // when
    MessageDto result = messageService.update(messageId, request);

    // then
    assertThat(result).isSameAs(response);

    then(messageRepository).should().findById(messageId);
    then(messageMapper).should().toDto(message);
  }

  @Test
  @DisplayName("메시지 수정 실패 - 메시지 없음")
  void update_fail_messageNotFound() {
    // given
    UUID messageId = UUID.randomUUID();

    MessageUpdateRequest request = new MessageUpdateRequest("수정된 메시지");

    given(messageRepository.findById(messageId))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.update(messageId, request))
        .isInstanceOf(MessageNotFoundException.class);

    then(messageRepository).should().findById(messageId);
    then(messageMapper).should(never()).toDto(any(Message.class));
  }

  @Test
  @DisplayName("메시지 삭제 성공")
  void delete_success() {
    // given
    UUID messageId = UUID.randomUUID();

    given(messageRepository.existsById(messageId))
        .willReturn(true);

    // when
    messageService.delete(messageId);

    // then
    then(messageRepository).should().existsById(messageId);
    then(messageRepository).should().deleteById(messageId);
  }

  @Test
  @DisplayName("메시지 삭제 실패 - 메시지 없음")
  void delete_fail_messageNotFound() {
    // given
    UUID messageId = UUID.randomUUID();

    given(messageRepository.existsById(messageId))
        .willReturn(false);

    // when & then
    assertThatThrownBy(() -> messageService.delete(messageId))
        .isInstanceOf(MessageNotFoundException.class);

    then(messageRepository).should().existsById(messageId);
    then(messageRepository).should(never()).deleteById(messageId);
  }

  @Test
  @DisplayName("채널 ID로 메시지 목록 조회 성공")
  void findAllByChannelId_success() {
    // given
    UUID channelId = UUID.randomUUID();
    Instant cursor = Instant.now();
    Pageable pageable = PageRequest.of(0, 10);

    Channel channel = new Channel(ChannelType.PUBLIC, "공지", "공지 채널");
    User author = new User("minji", "minji@test.com", "1234", null);
    Message message = new Message("메시지", channel, author, List.of());

    MessageDto messageDto = mock(MessageDto.class);
    PageResponse<MessageDto> response = mock(PageResponse.class);

    given(messageDto.createdAt())
        .willReturn(Instant.now());

    Slice<Message> messageSlice = new SliceImpl<>(
        List.of(message),
        pageable,
        false
    );

    given(messageRepository.findAllByChannelIdWithAuthor(channelId, cursor, pageable))
        .willReturn(messageSlice);

    given(messageMapper.toDto(message))
        .willReturn(messageDto);

    given(pageResponseMapper.fromSlice(any(Slice.class), any(Instant.class)))
        .willReturn(response);

    // when
    PageResponse<MessageDto> result = messageService.findAllByChannelId(
        channelId,
        cursor,
        pageable
    );

    // then
    assertThat(result).isSameAs(response);

    then(messageRepository).should()
        .findAllByChannelIdWithAuthor(channelId, cursor, pageable);
    then(messageMapper).should().toDto(message);
    then(pageResponseMapper).should().fromSlice(any(Slice.class), any(Instant.class));
  }

  @Test
  @DisplayName("채널 ID로 메시지 목록 조회 실패 - Repository 예외 발생")
  void findAllByChannelId_fail_repositoryException() {
    // given
    UUID channelId = UUID.randomUUID();
    Instant cursor = Instant.now();
    Pageable pageable = PageRequest.of(0, 10);

    given(messageRepository.findAllByChannelIdWithAuthor(channelId, cursor, pageable))
        .willThrow(new RuntimeException("메시지 목록 조회 실패"));

    // when & then
    assertThatThrownBy(() -> messageService.findAllByChannelId(channelId, cursor, pageable))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("메시지 목록 조회 실패");

    then(messageRepository).should()
        .findAllByChannelIdWithAuthor(channelId, cursor, pageable);
    then(messageMapper).should(never()).toDto(any(Message.class));
    then(pageResponseMapper).should(never()).fromSlice(any(Slice.class), any(Instant.class));
  }
}