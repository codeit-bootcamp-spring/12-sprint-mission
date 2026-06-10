package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private BasicMessageService messageService;

  @Test
  @DisplayName("메시지를 생성할 수 있다")
  void create_message_success() {
    // given
    UUID authorId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();

    MessageCreateRequest request = new MessageCreateRequest(
        "안녕하세요",
        channelId,
        authorId
    );

    User author = createUser("user@test.com", "user");
    Channel channel = Channel.createPublic("공개 채널", "설명");

    given(userRepository.findById(authorId))
        .willReturn(Optional.of(author));
    given(channelRepository.findById(channelId))
        .willReturn(Optional.of(channel));
    given(messageRepository.save(any(Message.class)))
        .willAnswer(invocation -> invocation.getArgument(0));

    // when
    MessageResponse response = messageService.create(request, List.of());

    // then
    assertThat(response).isNotNull();

    verify(messageRepository).save(any(Message.class));
  }

  @Test
  @DisplayName("메시지 생성 시 작성자가 없으면 예외가 발생한다")
  void create_message_fail_userNotFound() {
    // given
    UUID authorId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();

    MessageCreateRequest request = new MessageCreateRequest(
        "안녕하세요",
        channelId,
        authorId
    );

    given(userRepository.findById(authorId))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(UserNotFoundException.class);

    verify(messageRepository, never()).save(any(Message.class));
  }

  @Test
  @DisplayName("메시지를 수정할 수 있다")
  void update_message_success() {
    // given
    UUID messageId = UUID.randomUUID();

    User author = createUser("user@test.com", "user");
    Channel channel = Channel.createPublic("공개 채널", "설명");

    Message message = new Message(
        "기존 메시지",
        channel,
        author,
        List.of()
    );

    MessageUpdateRequest request = new MessageUpdateRequest("수정 메시지");

    given(messageRepository.findById(messageId))
        .willReturn(Optional.of(message));
    given(userRepository.findById(author.getId()))
        .willReturn(Optional.of(author));
    given(channelRepository.findById(channel.getId()))
        .willReturn(Optional.of(channel));
    given(messageRepository.save(any(Message.class)))
        .willAnswer(invocation -> invocation.getArgument(0));

    // when
    MessageResponse response = messageService.update(messageId, request);

    // then
    assertThat(response).isNotNull();

    verify(messageRepository).save(message);
  }

  @Test
  @DisplayName("존재하지 않는 메시지 수정 시 예외가 발생한다")
  void update_message_fail_messageNotFound() {
    // given
    UUID messageId = UUID.randomUUID();

    MessageUpdateRequest request = new MessageUpdateRequest("수정 메시지");

    given(messageRepository.findById(messageId))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.update(messageId, request))
        .isInstanceOf(MessageNotFoundException.class);

    verify(messageRepository, never()).save(any(Message.class));
  }

  @Test
  @DisplayName("메시지를 삭제할 수 있다")
  void delete_message_success() {
    // given
    UUID messageId = UUID.randomUUID();

    User author = createUser("user@test.com", "user");
    Channel channel = Channel.createPublic("공개 채널", "설명");

    Message message = new Message(
        "삭제할 메시지",
        channel,
        author,
        List.of()
    );

    given(messageRepository.findById(messageId))
        .willReturn(Optional.of(message));

    // when
    messageService.delete(messageId);

    // then
    verify(messageRepository).deleteById(messageId);
    verify(binaryContentRepository, never()).deleteAll(any());
  }

  @Test
  @DisplayName("존재하지 않는 메시지 삭제 시 예외가 발생한다")
  void delete_message_fail_messageNotFound() {
    // given
    UUID messageId = UUID.randomUUID();

    given(messageRepository.findById(messageId))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.delete(messageId))
        .isInstanceOf(MessageNotFoundException.class);

    verify(messageRepository, never()).deleteById(messageId);
  }

  @Test
  @DisplayName("채널 ID로 메시지 목록을 조회할 수 있다")
  void find_by_channel_id_success() {
    // given
    UUID channelId = UUID.randomUUID();

    User author = createUser("user@test.com", "user");
    Channel channel = Channel.createPublic("공개 채널", "설명");

    Message message1 = new Message(
        "메시지1",
        channel,
        author,
        List.of()
    );

    Message message2 = new Message(
        "메시지2",
        channel,
        author,
        List.of()
    );

    Slice<Message> messageSlice = new SliceImpl<>(List.of(message1, message2));

    given(channelRepository.findById(channelId))
        .willReturn(Optional.of(channel));
    given(messageRepository.findAllByChannelOrderByCreatedAtDesc(
        any(Channel.class),
        any(Pageable.class)
    )).willReturn(messageSlice);

    // when
    PageResponse<MessageResponse> response = messageService.findAllByChannelId(channelId, 0);

    // then
    assertThat(response).isNotNull();

    verify(messageRepository).findAllByChannelOrderByCreatedAtDesc(
        any(Channel.class),
        any(Pageable.class)
    );
  }

  @Test
  @DisplayName("존재하지 않는 채널의 메시지 목록 조회 시 예외가 발생한다")
  void find_by_channel_id_fail_channelNotFound() {
    // given
    UUID channelId = UUID.randomUUID();

    given(channelRepository.findById(channelId))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> messageService.findAllByChannelId(channelId, 0))
        .isInstanceOf(ChannelNotFoundException.class);

    verify(messageRepository, never()).findAllByChannelOrderByCreatedAtDesc(
        any(Channel.class),
        any(Pageable.class)
    );
  }

  private User createUser(String email, String username) {
    User user = new User(
        email,
        username,
        "password",
        null
    );
    user.initStatus();
    return user;
  }
}