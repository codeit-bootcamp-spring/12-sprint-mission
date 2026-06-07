package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.ArgumentMatchers.any;

import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapperImpl;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

  @InjectMocks
  private BasicMessageService messageService;

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private MessageMapper messageMapper;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private BinaryContentStorage binaryContentStorage;

  @Spy
  private PageResponseMapper pageResponseMapper = new PageResponseMapperImpl();

  // create
  @Test
  @DisplayName("create 성공")
  void create_success() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest(channelId, authorId, "안녕하세요");

    Channel mockChannel = new Channel(ChannelType.PUBLIC, "공지", null);
    User mockAuthor = new User("홍길동", "hong@example.com", "pw", null);
    Message mockMessage = new Message("안녕하세요", mockChannel, mockAuthor, List.of());
    MessageResponse mockResponse = new MessageResponse(
        UUID.randomUUID(), Instant.now(), Instant.now(), "안녕하세요", channelId, null, List.of()
    );

    given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));
    given(userRepository.findById(authorId)).willReturn(Optional.of(mockAuthor));
    given(messageRepository.save(any(Message.class))).willReturn(mockMessage);
    given(messageMapper.toDto(any(Message.class))).willReturn(mockResponse);

    // when
    MessageResponse result = messageService.create(request, List.of());

    // then
    assertThat(result.content()).isEqualTo("안녕하세요");
    then(messageRepository).should().save(any(Message.class));
  }

  @Test
  @DisplayName("create 실패 - 존재하지 않는 채널")
  void create_fail_channelNotFound() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest(channelId, authorId, "안녕하세요");

    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(NoSuchElementException.class);
    then(userRepository).shouldHaveNoInteractions();
    then(messageRepository).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("create 실패 - 존재하지 않는 작성자")
  void create_fail_authorNotFound() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest(channelId, authorId, "안녕하세요");
    Channel mockChannel = new Channel(ChannelType.PUBLIC, "공지", null);

    given(channelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));
    given(userRepository.findById(authorId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(NoSuchElementException.class);
    then(messageRepository).shouldHaveNoInteractions();
  }

  // update
  @Test
  @DisplayName("update 성공")
  void update_success() {
    // given
    UUID messageId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용");
    Channel mockChannel = new Channel(ChannelType.PUBLIC, "공지", null);
    User mockAuthor = new User("홍길동", "hong@example.com", "pw", null);
    Message mockMessage = new Message("원본 내용", mockChannel, mockAuthor, List.of());
    MessageResponse mockResponse = new MessageResponse(
        messageId, Instant.now(), Instant.now(), "수정된 내용", channelId, null, List.of()
    );

    given(messageRepository.findById(messageId)).willReturn(Optional.of(mockMessage));
    given(messageMapper.toDto(any(Message.class))).willReturn(mockResponse);

    // when
    MessageResponse result = messageService.update(messageId, request);

    // then
    assertThat(result.content()).isEqualTo("수정된 내용");
  }

  @Test
  @DisplayName("update 실패 - 존재하지 않는 메시지")
  void update_fail_messageNotFound() {
    // given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용");
    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.update(messageId, request))
        .isInstanceOf(NoSuchElementException.class);
  }

  // delete
  @Test
  @DisplayName("delete 성공")
  void delete_success() {
    // given
    UUID messageId = UUID.randomUUID();
    given(messageRepository.existsById(messageId)).willReturn(true);

    // when
    messageService.delete(messageId);

    // then
    then(messageRepository).should().deleteById(messageId);
  }

  @Test
  @DisplayName("delete 실패 - 존재하지 않는 메시지")
  void delete_fail_messageNotFound() {
    // given
    UUID messageId = UUID.randomUUID();
    given(messageRepository.existsById(messageId)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> messageService.delete(messageId))
        .isInstanceOf(NoSuchElementException.class);
    then(messageRepository).should().existsById(messageId);
    then(messageRepository).shouldHaveNoMoreInteractions();
  }

  // findAllByChannelId
  @Test
  @DisplayName("findAllByChannelId 성공")
  void findAllByChannelId_success() {
    // given
    UUID channelId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 50);
    Instant cursor = Instant.now();

    MessageResponse mockResponse = new MessageResponse(
        UUID.randomUUID(), Instant.now(), Instant.now(), "메시지", channelId, null, List.of()
    );
    SliceImpl<MessageResponse> mockSlice = new SliceImpl<>(List.of(mockResponse), pageable, false);

    given(messageRepository.findAllByChannelIdWithAuthor(any(UUID.class), any(Instant.class),
        any(Pageable.class)))
        .willReturn(mockSlice);

    // when
    PageResponse<MessageResponse> result = messageService.findAllByChannelId(channelId, cursor,
        pageable);

    // then
    assertThat(result.content()).hasSize(1);
    assertThat(result.hasNext()).isFalse();
  }

  @Test
  @DisplayName("findAllByChannelId - cursor null이면 현재 시각 기준으로 조회")
  void findAllByChannelId_nullCursor() {
    // given
    UUID channelId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 50);
    SliceImpl<MessageResponse> mockSlice = new SliceImpl<>(List.of(), pageable, false);

    given(messageRepository.findAllByChannelIdWithAuthor(any(UUID.class), any(Instant.class),
        any(Pageable.class)))
        .willReturn(mockSlice);

    // when
    PageResponse<MessageResponse> result = messageService.findAllByChannelId(channelId, null,
        pageable);

    // then
    assertThat(result.content()).isEmpty();
    assertThat(result.hasNext()).isFalse();
  }
}