package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.channel.ChannelPrivateCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelPublicCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelCannotUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private MessageRepository messageRepository;

  @InjectMocks
  private BasicChannelService channelService;

  @Test
  @DisplayName("공개 채널을 생성할 수 있다")
  void create_public_channel_success() {
    // given
    ChannelPublicCreateRequest request = new ChannelPublicCreateRequest(
        "공개 채널",
        "공개 채널 설명"
    );

    Channel channel = Channel.createPublic(
        request.name(),
        request.description()
    );
    given(channelRepository.save(any(Channel.class)))
        .willReturn(channel);

    // when
    ChannelResponse response = channelService.createPublic(request);

    // then
    assertThat(response.type()).isEqualTo(ChannelType.PUBLIC);
    assertThat(response.name()).isEqualTo(request.name());
    assertThat(response.description()).isEqualTo(request.description());
    assertThat(response.participants()).isEmpty();

    verify(channelRepository).save(any(Channel.class));
  }

  @Test
  @DisplayName("비공개 채널을 생성할 수 있다")
  void create_private_channel_success() {
    // given
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();

    ChannelPrivateCreateRequest request = new ChannelPrivateCreateRequest(
        "비공개 채널",
        List.of(userId1, userId2)
    );

    Channel channel = Channel.createPrivate(request.name());

    User user1 = createUser("user1@test.com", "user1");
    User user2 = createUser("user2@test.com", "user2");

    given(channelRepository.save(any(Channel.class)))
        .willReturn(channel);
    given(userRepository.findById(userId1))
        .willReturn(Optional.of(user1));
    given(userRepository.findById(userId2))
        .willReturn(Optional.of(user2));

    // when
    ChannelResponse response = channelService.createPrivate(request);

    // then
    assertThat(response.type()).isEqualTo(ChannelType.PRIVATE);
    assertThat(response.name()).isEqualTo(request.name());
    assertThat(response.participants()).hasSize(2);

    verify(channelRepository).save(any(Channel.class));
    verify(readStatusRepository, times(2)).save(any(ReadStatus.class));
  }

  @Test
  @DisplayName("비공개 채널 생성 시 사용자가 없으면 예외가 발생한다")
  void create_private_channel_fail_userNotFound() {
    // given
    UUID userId = UUID.randomUUID();

    ChannelPrivateCreateRequest request = new ChannelPrivateCreateRequest(
        "비공개 채널",
        List.of(userId)
    );

    Channel channel = Channel.createPrivate(request.name());

    given(channelRepository.save(any(Channel.class)))
        .willReturn(channel);
    given(userRepository.findById(userId))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> channelService.createPrivate(request))
        .isInstanceOf(UserNotFoundException.class);

    verify(readStatusRepository, never()).save(any(ReadStatus.class));
  }

  @Test
  @DisplayName("공개 채널을 수정할 수 있다")
  void update_channel_success() {
    // given
    UUID channelId = UUID.randomUUID();

    Channel channel = Channel.createPublic(
        "기존 채널",
        "기존 설명"
    );

    ChannelUpdateRequest request = new ChannelUpdateRequest(
        "수정 채널",
        "수정 설명"
    );

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(channelRepository.save(any(Channel.class))).willAnswer(
        invocation -> invocation.getArgument(0));
    given(messageRepository.findTopByChannelOrderByCreatedAtDesc(channel)).willReturn(
        Optional.empty());

    // when
    ChannelResponse response = channelService.update(channelId, request);

    // then
    assertThat(response.name()).isEqualTo(request.newName());
    assertThat(response.description()).isEqualTo(request.newDescription());
    assertThat(response.type()).isEqualTo(ChannelType.PUBLIC);

    verify(channelRepository).save(channel);
  }

  @Test
  @DisplayName("비공개 채널은 수정할 수 없다")
  void update_channel_fail_privateChannel() {
    // given
    UUID channelId = UUID.randomUUID();

    Channel channel = Channel.createPrivate("비공개 채널");

    ChannelUpdateRequest request = new ChannelUpdateRequest(
        "수정 채널",
        "수정 설명"
    );

    given(channelRepository.findById(channelId))
        .willReturn(Optional.of(channel));

    // when & then
    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(PrivateChannelCannotUpdateException.class);

    verify(channelRepository, never()).save(any(Channel.class));
  }

  @Test
  @DisplayName("채널을 삭제할 수 있다")
  void delete_channel_success() {
    // given
    UUID channelId = UUID.randomUUID();

    Channel channel = Channel.createPublic(
        "공개 채널",
        "공개 채널 설명"
    );

    given(channelRepository.findById(channelId))
        .willReturn(Optional.of(channel));

    // when
    channelService.delete(channelId);

    // then
    verify(messageRepository).deleteByChannelId(channelId);
    verify(readStatusRepository).deleteByChannelId(channelId);
    verify(channelRepository).deleteById(channelId);
  }

  @Test
  @DisplayName("존재하지 않는 채널 삭제 시 예외가 발생한다")
  void delete_channel_fail_channelNotFound() {
    // given
    UUID channelId = UUID.randomUUID();

    given(channelRepository.findById(channelId))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> channelService.delete(channelId))
        .isInstanceOf(ChannelNotFoundException.class);

    verify(messageRepository, never()).deleteByChannelId(channelId);
    verify(readStatusRepository, never()).deleteByChannelId(channelId);
    verify(channelRepository, never()).deleteById(channelId);
  }

  @Test
  @DisplayName("사용자가 접근 가능한 채널 목록을 조회할 수 있다")
  void find_by_user_id_success() {
    // given
    UUID userId = UUID.randomUUID();

    User user = createUser("user@test.com", "user");

    Channel publicChannel = Channel.createPublic(
        "공개 채널",
        "공개 설명"
    );

    Channel privateChannel = Channel.createPrivate("비공개 채널");

    ReadStatus readStatus = new ReadStatus(
        user,
        privateChannel,
        Instant.now()
    );

    given(userRepository.findById(userId))
        .willReturn(Optional.of(user));
    given(channelRepository.findAll())
        .willReturn(List.of(publicChannel, privateChannel));
    given(readStatusRepository.findByUserIdAndChannelId(userId, privateChannel.getId()))
        .willReturn(Optional.of(readStatus));
    given(readStatusRepository.findAllByChannelId(privateChannel.getId()))
        .willReturn(List.of(readStatus));
    given(messageRepository.findTopByChannelOrderByCreatedAtDesc(any(Channel.class)))
        .willReturn(Optional.empty());

    // when
    List<ChannelResponse> responses = channelService.findAllByUserId(userId);

    // then
    assertThat(responses).hasSize(2);
    assertThat(responses)
        .extracting(ChannelResponse::name)
        .contains("공개 채널", "비공개 채널");
  }

  @Test
  @DisplayName("존재하지 않는 사용자로 채널 목록 조회 시 예외가 발생한다")
  void find_by_user_id_fail_userNotFound() {
    // given
    UUID userId = UUID.randomUUID();

    given(userRepository.findById(userId))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> channelService.findAllByUserId(userId))
        .isInstanceOf(UserNotFoundException.class);

    verify(channelRepository, never()).findAll();
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